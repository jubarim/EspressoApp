package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.AuthUser

interface AuthRepository {

    /**
     * Step 1 of registration: signs in anonymously (required by the firebase-web-authn extension)
     * then fetches and returns the WebAuthn public-key creation challenge JSON.
     */
    suspend fun prepareRegistration(displayName: String): Result<String>

    /**
     * Step 2 of registration: sends the signed [registrationResponseJson] from CredentialManager
     * to the Cloud Function for verification.
     */
    suspend fun completeRegistration(registrationResponseJson: String): Result<Unit>

    /**
     * Step 1 of sign-in: fetches and returns the WebAuthn public-key assertion challenge JSON.
     */
    suspend fun getLoginChallenge(displayName: String): Result<String>

    /**
     * Step 2 of sign-in: verifies the signed [authResponseJson] with the Cloud Function and
     * exchanges the returned Firebase Custom Token for a live Firebase Auth session.
     */
    suspend fun completeLogin(authResponseJson: String): Result<Unit>

    /** Signs the current user out, clearing the local Firebase Auth session. */
    suspend fun signOut()

    /** Emits the currently authenticated user, or null when signed out. */
    fun currentUser(): Flow<AuthUser?>

    /** Persists [name] locally so the auth screen can greet the user on return visits. */
    suspend fun saveDisplayName(name: String)

    /**
     * Emits the locally persisted display name, or null if none has been saved yet.
     * Used to decide which auth screen variant to show.
     */
    fun getSavedDisplayName(): Flow<String?>

    /** Removes the locally persisted display name, showing the new-user screen on next launch. */
    suspend fun clearSavedDisplayName()
}
