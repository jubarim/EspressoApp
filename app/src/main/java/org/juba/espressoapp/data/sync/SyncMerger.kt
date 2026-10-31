package org.juba.espressoapp.data.sync

/**
 * Uploads the local records the cloud does not have, or that are newer locally.
 *
 * Runs once per session, before the listeners attach, so the push direction and the pull
 * direction never race. Only the push direction happens here. Remote records that are newer
 * stay for the listeners to apply, which keeps last write wins in a single place.
 */
interface SyncMerger {

    /**
     * Compares the local database with the cloud for [uid] and pushes everything the cloud is
     * missing or has an older copy of. Safe to call on every start: pushes are idempotent, so
     * an interrupted merge simply resumes next time.
     */
    suspend fun merge(uid: String): Result<Unit>
}
