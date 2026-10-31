package org.juba.espressoapp.data.remote

/**
 * Collections mirrored to Firestore under `users/{uid}/<path>`.
 *
 * The declaration order is the foreign key dependency order used when pushing and applying
 * remote changes: a parent is written or applied before any record that references it.
 */
enum class SyncCollection(val path: String) {
    ROASTERS("roasters"),
    COFFEE_BEANS("coffee_beans"),
    GRINDERS("grinders"),
    ESPRESSO_MACHINES("espresso_machines"),
    FILTER_BASKETS("filter_baskets"),
    SHOT_LOGS("shot_logs"),
}
