package org.juba.espressoapp.data.remote

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject

private const val TAG = "EspressoSync"

/** [RemoteSource] backed by Cloud Firestore snapshot listeners. */
class FirestoreRemoteSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : RemoteSource {

    override fun observe(uid: String, collection: SyncCollection): Flow<RemoteChange> =
        callbackFlow {
            AppLog.d(TAG) { "listener attach ${collection.path} uid=$uid" }
            val registration = firestore
                .remoteCollection(uid, collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        AppLog.e(TAG, error) { "listener error ${collection.path} uid=$uid" }
                        close(error)
                        return@addSnapshotListener
                    }
                    val changes = snapshot?.documentChanges.orEmpty()
                    AppLog.d(TAG) { "listener ${collection.path} changes=${changes.size}" }
                    changes.forEach { change ->
                        trySend(
                            RemoteChange(
                                document = RemoteDocument(
                                    collection = collection,
                                    id = change.document.id,
                                    fields = change.document.data.orEmpty(),
                                ),
                                type = change.type.toRemoteChangeType(),
                            ),
                        )
                    }
                }
            awaitClose {
                AppLog.d(TAG) { "listener detach ${collection.path}" }
                registration.remove()
            }
        }
}

private fun DocumentChange.Type.toRemoteChangeType(): RemoteChangeType = when (this) {
    DocumentChange.Type.ADDED -> RemoteChangeType.Added
    DocumentChange.Type.MODIFIED -> RemoteChangeType.Modified
    DocumentChange.Type.REMOVED -> RemoteChangeType.Removed
}
