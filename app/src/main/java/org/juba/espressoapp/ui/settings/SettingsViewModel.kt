package org.juba.espressoapp.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.juba.espressoapp.data.sync.SyncEngine
import org.juba.espressoapp.data.sync.SyncHealthCheck
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.domain.repository.BackupRepository
import org.juba.espressoapp.domain.repository.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val syncHealthCheck: SyncHealthCheck,
    private val syncEngine: SyncEngine,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = combine(
        _state,
        authRepository.currentUser(),
        syncEngine.status,
        preferencesRepository.lastSyncAt(),
    ) { state, user, status, lastSync ->
        state.copy(account = user, syncStatus = status, lastSyncAt = lastSync)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun export(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(exportState = BackupOperation.InProgress) }
            val result = backupRepository.exportBackup(uri)
            _state.update {
                it.copy(
                    exportState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Export failed"),
                )
            }
        }
    }

    fun import(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(importState = BackupOperation.InProgress) }
            val result = backupRepository.importBackup(uri)
            _state.update {
                it.copy(
                    importState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Import failed"),
                )
            }
        }
    }

    fun clearExportState() = _state.update { it.copy(exportState = BackupOperation.Idle) }
    fun clearImportState() = _state.update { it.copy(importState = BackupOperation.Idle) }

    /** Debug-only end-to-end check of the Firestore wiring. */
    fun runSyncCheck() {
        viewModelScope.launch {
            _state.update { it.copy(syncCheckState = BackupOperation.InProgress) }
            val result = syncHealthCheck.run()
            _state.update {
                it.copy(
                    syncCheckState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Sync check failed"),
                )
            }
        }
    }

    fun clearSyncCheckState() = _state.update { it.copy(syncCheckState = BackupOperation.Idle) }

    /** Merges local changes again and reattaches the listeners. */
    fun syncNow() = syncEngine.syncNow()

    /**
     * Waits for queued Firestore writes before signing out. On timeout the sign-out is blocked so
     * the last offline edits are not silently dropped, and the screen shows an error.
     *
     * The onboarding flag is reset first, so the sync engine sees the welcome screen, not a guest
     * session, and keeps the local data for a cheap re-sign-in.
     */
    fun signOut() {
        if (_state.value.isSigningOut) return
        viewModelScope.launch {
            _state.update { it.copy(isSigningOut = true, signOutFailed = false) }
            val flushed = withTimeoutOrNull(SIGN_OUT_TIMEOUT_MS) {
                syncEngine.awaitPendingWrites().isSuccess
            } ?: false
            if (!flushed) {
                _state.update { it.copy(isSigningOut = false, signOutFailed = true) }
                return@launch
            }
            preferencesRepository.resetOnboarding()
            authRepository.signOut()
            _state.update { it.copy(isSigningOut = false) }
        }
    }

    fun clearSignOutFailure() = _state.update { it.copy(signOutFailed = false) }

    private companion object {
        const val SIGN_OUT_TIMEOUT_MS = 5_000L
    }
}
