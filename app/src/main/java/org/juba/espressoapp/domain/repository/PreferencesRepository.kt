package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Persists app-level choices that must survive restarts but are not tied to a signed-in account.
 */
interface PreferencesRepository {

    /**
     * Emits true once the user has made the first-launch choice (sign in with Google or
     * continue without an account). Used to decide whether to show the welcome screen.
     */
    fun isOnboardingComplete(): Flow<Boolean>

    /** Marks the first-launch choice as made. */
    suspend fun setOnboardingComplete()

    /**
     * Clears the first-launch choice so the welcome screen is shown again.
     * Called on sign-out, which is an explicit exit back to the sign-in screen.
     */
    suspend fun resetOnboarding()
}
