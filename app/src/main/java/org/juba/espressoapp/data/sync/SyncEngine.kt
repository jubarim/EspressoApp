package org.juba.espressoapp.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.juba.espressoapp.data.di.ApplicationScope
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.remote.RemoteSource
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.domain.repository.PreferencesRepository
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EspressoSync"

/**
 * Owns the sync lifecycle.
 *
 * The session is derived from the auth state plus the onboarding flag:
 * - a signed-in user claims the local data and starts the pull
 * - a guest session wipes data left behind by a previous account
 * - the welcome screen does nothing, so signing out does not wipe and re-signing into the same
 *   account stays cheap
 *
 * Listeners follow the signed-in user and are cancelled when the session changes. Sync runs
 * while the process lives, there is no background worker.
 */
@Singleton
class SyncEngine @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val remoteSource: RemoteSource,
    private val applier: SyncApplier,
    private val merger: SyncMerger,
    private val database: AppDatabase,
) {
    private var job: Job? = null

    /** Starts observing the session. Safe to call more than once. */
    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            combine(
                preferencesRepository.isOnboardingComplete(),
                authRepository.currentUser(),
            ) { onboardingComplete, user ->
                when {
                    user != null -> SyncSession.User(user.uid)
                    onboardingComplete -> SyncSession.Guest
                    else -> SyncSession.Welcome
                }
            }
                .distinctUntilChanged()
                .collectLatest { session ->
                    when (session) {
                        is SyncSession.User -> runUserSession(session.uid)
                        SyncSession.Guest -> wipeGuestData()
                        SyncSession.Welcome -> AppLog.d(TAG) { "welcome screen, sync idle" }
                    }
                }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private suspend fun runUserSession(uid: String) {
        prepareFor(uid)
        AppLog.d(TAG) { "pull start uid=$uid" }
        coroutineScope {
            SyncCollection.entries.forEach { collection ->
                launch {
                    remoteSource.observe(uid, collection)
                        .catch { error ->
                            AppLog.e(TAG, error) { "listener failed ${collection.path}" }
                        }
                        .collect { change -> applier.apply(change) }
                }
            }
        }
        AppLog.d(TAG) { "pull stopped uid=$uid" }
    }

    /**
     * Claims the local data for [uid] and merges it with the cloud. A different owner means the
     * previous account's data must be wiped before the new one is pulled in.
     */
    private suspend fun prepareFor(uid: String) {
        val owner = preferencesRepository.dataOwnerUid().first()
        if (owner != null && owner != uid) {
            AppLog.d(TAG) { "identity changed from $owner to $uid, wiping local data" }
            wipeLocalData()
        } else {
            merger.merge(uid).onFailure { error ->
                AppLog.e(TAG, error) { "merge failed uid=$uid" }
            }
        }
        preferencesRepository.setDataOwnerUid(uid)
    }

    private suspend fun wipeGuestData() {
        val owner = preferencesRepository.dataOwnerUid().first() ?: return
        AppLog.d(TAG) { "guest session after $owner, wiping local data" }
        wipeLocalData()
        preferencesRepository.setDataOwnerUid(null)
    }

    private suspend fun wipeLocalData() {
        applier.clearPending()
        database.clearAllTables()
        AppLog.d(TAG) { "local data wiped" }
    }
}

private sealed interface SyncSession {
    data class User(val uid: String) : SyncSession
    data object Guest : SyncSession
    data object Welcome : SyncSession
}
