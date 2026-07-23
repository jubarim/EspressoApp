package org.juba.espressoapp.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.domain.repository.BackupRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun export(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(exportState = BackupOperation.InProgress) }
            val result = backupRepository.exportBackup(uri)
            _uiState.update {
                it.copy(
                    exportState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Export failed"),
                )
            }
        }
    }

    fun import(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(importState = BackupOperation.InProgress) }
            val result = backupRepository.importBackup(uri)
            _uiState.update {
                it.copy(
                    importState = if (result.isSuccess) BackupOperation.Success
                    else BackupOperation.Error(result.exceptionOrNull()?.message ?: "Import failed"),
                )
            }
        }
    }

    fun clearExportState() = _uiState.update { it.copy(exportState = BackupOperation.Idle) }
    fun clearImportState() = _uiState.update { it.copy(importState = BackupOperation.Idle) }

    /** Signs the user out. The app-level auth state observer handles navigation to the auth screen. */
    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }
}
