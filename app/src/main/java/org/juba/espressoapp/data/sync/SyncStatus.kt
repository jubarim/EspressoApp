package org.juba.espressoapp.data.sync

/** High level state of the background sync, surfaced in Settings. */
sealed interface SyncStatus {

    /** Not signed in, or nothing to sync yet. */
    data object Idle : SyncStatus

    /** Listeners attached and changes being pushed or applied. */
    data object Syncing : SyncStatus

    /** Last operation failed. [message] is user facing. */
    data class Error(val message: String) : SyncStatus
}
