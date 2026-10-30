package org.juba.espressoapp.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.juba.espressoapp.data.di.ApplicationScope
import org.juba.espressoapp.data.remote.RemoteSource
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EspressoSync"

/**
 * Keeps Room in sync with the remote changes of the signed-in user.
 *
 * Listeners follow the signed-in user: they attach when an uid appears and detach when it
 * changes or disappears. Sync runs while the process lives, there is no background worker.
 */
@Singleton
class SyncEngine @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val remoteSource: RemoteSource,
    private val applier: SyncApplier,
) {
    private var job: Job? = null

    /** Starts observing the auth state. Safe to call more than once. */
    fun start() {
        if (job?.isActive == true) return

        job = scope.launch {
            authRepository.currentUser()
                .map { it?.uid }
                .distinctUntilChanged()
                .collectLatest { uid ->
                    if (uid == null) {
                        AppLog.d(TAG) { "pull idle, no signed-in user" }
                        return@collectLatest
                    }

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
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
