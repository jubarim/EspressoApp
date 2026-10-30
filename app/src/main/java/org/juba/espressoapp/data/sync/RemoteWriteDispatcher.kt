package org.juba.espressoapp.data.sync

import org.juba.espressoapp.data.remote.RemoteDocument

/**
 * Queues a local change for a remote write.
 *
 * Implementations must not block the caller. The Room write has already succeeded, and the
 * remote write can take a while, especially offline, where Firestore buffers it until
 * connectivity returns.
 */
interface RemoteWriteDispatcher {

    /** Queues [document] when a user is signed in, and does nothing otherwise. */
    fun dispatch(document: RemoteDocument)
}
