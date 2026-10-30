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
) : ViewModel() {

    private val _backupState = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = combine(
        _backupState,
        authRepository.currentUser(),
    ) { state, user -> state.copy(account = user) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun export(uri: Uri) {
        viewModelScope.launch {
            _backupState.update { it.copy(exportState = BackupOperation.InProgress) }
            val result = backupRepository.exportBackup(uri)
            _backupState.update {
                it.copy(
                    exportState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Export failed"),
                )
            }
        }
    }

    fun import(uri: Uri) {
        viewModelScope.launch {
            _backupState.update { it.copy(importState = BackupOperation.InProgress) }
            val result = backupRepository.importBackup(uri)
            _backupState.update {
                it.copy(
                    importState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Import failed"),
                )
            }
        }
    }

    fun clearExportState() = _backupState.update { it.copy(exportState = BackupOperation.Idle) }
    fun clearImportState() = _backupState.update { it.copy(importState = BackupOperation.Idle) }

    /** Debug-only end-to-end check of the Firestore wiring. */
    fun runSyncCheck() {
        viewModelScope.launch {
            _backupState.update { it.copy(syncCheckState = BackupOperation.InProgress) }
            val result = syncHealthCheck.run()
            _backupState.update {
                it.copy(
                    syncCheckState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Sync check failed"),
                )
            }
        }
    }

    fun clearSyncCheckState() = _backupState.update { it.copy(syncCheckState = BackupOperation.Idle) }

    /** Signs the user out and returns the app to the welcome screen. */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            preferencesRepository.resetOnboarding()
        }
    }
}
