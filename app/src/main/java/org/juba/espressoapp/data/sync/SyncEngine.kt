package org.juba.espressoapp.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import org.juba.espressoapp.data.remote.SyncTarget
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
    private val syncTarget: SyncTarget,
    private val applier: SyncApplier,
    private val merger: SyncMerger,
    private val database: AppDatabase,
) {
    private val _status = MutableStateFlow<SyncStatus>(SyncStatus.Idle)

    /** Observable state, surfaced by the Settings screen. */
    val status: StateFlow<SyncStatus> = _status.asStateFlow()

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
                        SyncSession.Guest -> {
                            wipeGuestData()
                            _status.value = SyncStatus.Idle
                        }
                        SyncSession.Welcome -> {
                            AppLog.d(TAG) { "welcome screen, sync idle" }
                            _status.value = SyncStatus.Idle
                        }
                    }
                }
        }
    }

    /** Restarts the session, merging local changes again and reattaching the listeners. */
    fun syncNow() {
        job?.cancel()
        job = null
        start()
    }

    fun stop() {
        job?.cancel()
        job = null
        _status.value = SyncStatus.Idle
    }

    /**
     * Waits for queued Firestore writes to be acknowledged. Used before signing out so the
     * last edits made offline are not dropped.
     */
    suspend fun awaitPendingWrites(): Result<Unit> = syncTarget.awaitPendingWrites()

    private suspend fun runUserSession(uid: String) {
        _status.value = SyncStatus.Syncing
        val prepareSucceeded = prepareFor(uid)
        if (prepareSucceeded) _status.value = SyncStatus.Idle

        AppLog.d(TAG) { "pull start uid=$uid" }
        coroutineScope {
            SyncCollection.entries.forEach { collection ->
                launch {
                    remoteSource.observe(uid, collection)
                        .catch { error ->
                            AppLog.e(TAG, error) { "listener failed ${collection.path}" }
                            _status.value = SyncStatus.Error(error.message ?: "Listener failed")
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
     *
     * @return true when the start-up work succeeded, false when it failed and the status is set.
     */
    private suspend fun prepareFor(uid: String): Boolean {
        val owner = preferencesRepository.dataOwnerUid().first()
        if (owner != null && owner != uid) {
            AppLog.d(TAG) { "identity changed from $owner to $uid, wiping local data" }
            wipeLocalData()
            preferencesRepository.setDataOwnerUid(uid)
            return true
        }

        val result = merger.merge(uid)
        result.onFailure { error ->
            AppLog.e(TAG, error) { "merge failed uid=$uid" }
            _status.value = SyncStatus.Error(error.message ?: "Sync failed")
        }
        preferencesRepository.setDataOwnerUid(uid)
        return result.isSuccess
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
