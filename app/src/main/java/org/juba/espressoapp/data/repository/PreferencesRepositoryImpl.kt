package org.juba.espressoapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.domain.repository.PreferencesRepository
import javax.inject.Inject

private val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
private val DATA_OWNER_UID_KEY = stringPreferencesKey("data_owner_uid")
private val LAST_SYNC_AT_KEY = longPreferencesKey("last_sync_at")

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

    override fun dataOwnerUid(): Flow<String?> =
        dataStore.data.map { it[DATA_OWNER_UID_KEY] }

    override suspend fun setDataOwnerUid(uid: String?) {
        dataStore.edit { preferences ->
            if (uid == null) preferences.remove(DATA_OWNER_UID_KEY)
            else preferences[DATA_OWNER_UID_KEY] = uid
        }
    }

    override fun lastSyncAt(): Flow<Long?> =
        dataStore.data.map { it[LAST_SYNC_AT_KEY] }

    override suspend fun setLastSyncAt(timestamp: Long?) {
        dataStore.edit { preferences ->
            if (timestamp == null) preferences.remove(LAST_SYNC_AT_KEY)
            else preferences[LAST_SYNC_AT_KEY] = timestamp
        }
    }
}
