package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.AuthUser

interface AuthRepository {

    /**
     * Emits the currently signed-in user, or null in guest mode.
     * Guest mode is the default state and does not create a Firebase account.
     */
    fun currentUser(): Flow<AuthUser?>

    /**
     * Exchanges the Google ID token for a live Firebase Auth session.
     *
     * @param idToken ID token obtained from the Google sign-in flow.
     */
    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    /** Signs the current user out, returning the app to guest mode. */
    suspend fun signOut()
}
