package org.juba.espressoapp.domain.model

/**
 * Represents an authenticated user identity, decoupled from any remote auth provider.
 *
 * @param uid Stable unique identifier from Firebase Auth.
 * @param displayName Human-readable name supplied at passkey registration, or null if unavailable.
 */
data class AuthUser(
    val uid: String,
    val displayName: String?,
)
