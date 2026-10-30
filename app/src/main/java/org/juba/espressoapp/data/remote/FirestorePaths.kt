package org.juba.espressoapp.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

/** Root collection holding one document per account. */
private const val USERS_COLLECTION = "users"

/** Returns the Firestore subcollection holding [collection] for [uid]. */
internal fun FirebaseFirestore.remoteCollection(
    uid: String,
    collection: SyncCollection,
): CollectionReference = collection(USERS_COLLECTION).document(uid).collection(collection.path)
