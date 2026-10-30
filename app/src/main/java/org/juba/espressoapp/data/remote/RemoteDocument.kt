package org.juba.espressoapp.data.remote

/**
 * A document stored remotely, independent of Room.
 *
 * @param collection collection the document belongs to.
 * @param id document id, equal to the local primary key.
 * @param fields document body. Values must be types Firestore can store: String, Long,
 *   Double, Boolean, null, List and Map.
 */
data class RemoteDocument(
    val collection: SyncCollection,
    val id: String,
    val fields: Map<String, Any?>,
)

/** Kind of change reported by a [RemoteSource]. */
enum class RemoteChangeType { Added, Modified, Removed }

/** A single remote document change. */
data class RemoteChange(
    val document: RemoteDocument,
    val type: RemoteChangeType,
)
