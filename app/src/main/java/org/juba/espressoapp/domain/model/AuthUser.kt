package org.juba.espressoapp.domain.model

/**
 * Represents an authenticated user identity, decoupled from any remote auth provider.
 *
 * @param uid Stable unique identifier from Firebase Auth.
 * @param displayName Human-readable name from the Google account, or null if unavailable.
 * @param email Email address from the Google account, or null if unavailable.
 */
data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
)
