package org.juba.espressoapp.data.remote

import kotlinx.coroutines.flow.Flow

/** Emits changes to the remote documents of the signed-in user. */
interface RemoteSource {

    /**
     * Observes [collection] under `users/{uid}`.
     *
     * The first emission set is the full collection, later emissions are deltas. The flow
     * completes with an error when the listener fails, for example on `PERMISSION_DENIED`.
     */
    fun observe(uid: String, collection: SyncCollection): Flow<RemoteChange>

    /**
     * Reads [collection] once. Used by the initial merge to compare the cloud state with the
     * local database before deciding what to push.
     */
    suspend fun fetchAll(uid: String, collection: SyncCollection): List<RemoteDocument>
}
