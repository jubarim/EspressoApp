package org.juba.espressoapp.data.remote

import android.os.SystemClock
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject

private const val TAG = "EspressoSync"

/** Firestore limits a batch to 500 writes. */
private const val MAX_BATCH_SIZE = 500

/**
 * [SyncTarget] backed by Cloud Firestore.
 *
 * Writes use `set()` on a deterministic document id, so pushing the same record twice is
 * idempotent. Writes made while offline are queued by the SDK and flushed on reconnect.
 */
class FirestoreSyncTarget @Inject constructor(
    private val firestore: FirebaseFirestore,
) : SyncTarget {

    override suspend fun push(uid: String, documents: List<RemoteDocument>): Result<Unit> =
        runCatching {
            if (documents.isEmpty()) return@runCatching

            AppLog.d(TAG) {
                "push start uid=$uid docs=${documents.size} " +
                        documents.groupingBy { it.collection.path }.eachCount()
            }
            val startedAt = SystemClock.elapsedRealtime()

            documents
                .sortedBy { it.collection.ordinal }
                .chunked(MAX_BATCH_SIZE)
                .forEach { chunk ->
                    val batch = firestore.batch()
                    chunk.forEach { document ->
                        batch.set(documentReference(uid, document), document.fields)
                    }
                    batch.commit().await()
                }

            AppLog.d(TAG) {
                "push done uid=$uid docs=${documents.size} " +
                        "in ${SystemClock.elapsedRealtime() - startedAt} ms"
            }
        }.onFailure { error ->
            AppLog.e(TAG, error) { "push failed uid=$uid docs=${documents.size}" }
        }

    override suspend fun awaitPendingWrites(): Result<Unit> = runCatching {
        AppLog.d(TAG) { "awaiting pending writes" }
        firestore.waitForPendingWrites().await()
        AppLog.d(TAG) { "pending writes flushed" }
    }.onFailure { error ->
        AppLog.e(TAG, error) { "pending writes flush failed" }
    }

    private fun documentReference(uid: String, document: RemoteDocument): DocumentReference =
        firestore.remoteCollection(uid, document.collection).document(document.id)
}
