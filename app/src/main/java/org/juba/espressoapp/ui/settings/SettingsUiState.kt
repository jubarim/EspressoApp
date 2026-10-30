package org.juba.espressoapp.ui.settings

import org.juba.espressoapp.domain.model.AuthUser

sealed interface BackupOperation {
    data object Idle : BackupOperation
    data object InProgress : BackupOperation
    data object Success : BackupOperation
    data class Error(val message: String) : BackupOperation
}

data class SettingsUiState(
    val exportState: BackupOperation = BackupOperation.Idle,
    val importState: BackupOperation = BackupOperation.Idle,
    val syncCheckState: BackupOperation = BackupOperation.Idle,
    val account: AuthUser? = null,
)
