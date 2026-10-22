package org.juba.espressoapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.domain.repository.PreferencesRepository
import javax.inject.Inject

private val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {

    override fun isOnboardingComplete(): Flow<Boolean> =
        dataStore.data.map { it[ONBOARDING_COMPLETE_KEY] ?: false }

    override suspend fun setOnboardingComplete() {
        dataStore.edit { it[ONBOARDING_COMPLETE_KEY] = true }
    }

    override suspend fun resetOnboarding() {
        dataStore.edit { it.remove(ONBOARDING_COMPLETE_KEY) }
    }
}
