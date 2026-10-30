package org.juba.espressoapp.data.sync

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.RemoteSource
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.data.remote.SyncTarget
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject

/**
 * Debug-only end-to-end check of the Firestore wiring.
 *
 * Writes a tombstoned roaster for the signed-in user, waits for the write to be acknowledged
 * by the server, then waits for the document to come back through a snapshot listener. This
 * proves the project configuration, the auth token, the document path and the security rules
 * in one call.
 *
 * A tombstone (`isDeleted = true`) is used so the document stays invisible in the app if it is
 * ever pulled by the sync.
 */
class SyncHealthCheck @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncTarget: SyncTarget,
    private val remoteSource: RemoteSource,
) {

    suspend fun run(): Result<Unit> = runCatching {
        val uid = authRepository.currentUser().first()?.uid
            ?: error("Sign in with Google first")

        AppLog.d(TAG) { "health check start uid=$uid" }
        withTimeout(TIMEOUT_MS) {
            syncTarget.push(uid, listOf(document())).getOrThrow()
            syncTarget.awaitPendingWrites().getOrThrow()
            remoteSource.observe(uid, SyncCollection.ROASTERS)
                .first { it.document.id == DOCUMENT_ID }
        }
        AppLog.d(TAG) { "health check ok uid=$uid" }
    }.onFailure { error ->
        AppLog.e(TAG, error) { "health check failed" }
    }

    private fun document(): RemoteDocument {
        val now = System.currentTimeMillis()
        return RemoteDocument(
            collection = SyncCollection.ROASTERS,
            id = DOCUMENT_ID,
            fields = mapOf(
                "id" to DOCUMENT_ID,
                "name" to "Sync health check",
                "isDeleted" to true,
                "createdAt" to now,
                "updatedAt" to now,
                "docVersion" to DOCUMENT_VERSION,
            ),
        )
    }

    private companion object {
        const val TAG = "EspressoSync"
        const val DOCUMENT_ID = "sync-health-check"
        const val DOCUMENT_VERSION = 1L
        const val TIMEOUT_MS = 20_000L
    }
}
