package org.juba.espressoapp.data.remote

/** Writes local records to the remote store of the signed-in user. */
interface SyncTarget {

    /**
     * Writes [documents] under `users/{uid}`.
     *
     * Documents are written in [SyncCollection] declaration order so parents are stored
     * before their children. Returns a failure when there is no signed-in user, the
     * security rules deny the write, or the SDK cannot queue the write.
     */
    suspend fun push(uid: String, documents: List<RemoteDocument>): Result<Unit>

    /**
     * Suspends until every queued write has been acknowledged by the server.
     * Used before signing out so pending edits are not lost.
     */
    suspend fun awaitPendingWrites(): Result<Unit>
}
