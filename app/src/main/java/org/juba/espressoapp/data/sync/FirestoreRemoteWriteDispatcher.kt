package org.juba.espressoapp.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.juba.espressoapp.data.di.ApplicationScope
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.SyncTarget
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject

private const val TAG = "EspressoSync"

/**
 * [RemoteWriteDispatcher] that pushes to Firestore on the application scope.
 *
 * The write runs off the caller's scope so a slow or offline push never blocks the UI action
 * that triggered it. Guests are skipped, their data stays local and is merged on sign-in.
 */
class FirestoreRemoteWriteDispatcher @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val syncTarget: SyncTarget,
) : RemoteWriteDispatcher {

    override fun dispatch(document: RemoteDocument) {
        scope.launch {
            val uid = authRepository.currentUser().first()?.uid ?: return@launch
            syncTarget.push(uid, listOf(document)).onFailure { error ->
                AppLog.e(TAG, error) { "push failed ${document.collection.path}/${document.id}" }
            }
        }
    }
}
