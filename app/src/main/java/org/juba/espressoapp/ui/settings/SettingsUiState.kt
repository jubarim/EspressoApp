package org.juba.espressoapp.ui.settings

sealed interface BackupOperation {
    data object Idle : BackupOperation
    data object InProgress : BackupOperation
    data object Success : BackupOperation
    data class Error(val message: String) : BackupOperation
}

data class SettingsUiState(
    val exportState: BackupOperation = BackupOperation.Idle,
    val importState: BackupOperation = BackupOperation.Idle,
)
