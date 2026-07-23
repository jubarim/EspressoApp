package org.juba.espressoapp.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.main.LocalSnackbarHostState
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.util.Calendar

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current

    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let { viewModel.export(it) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            showImportConfirmDialog = true
        }
    }

    val exportSuccessMessage = stringResource(R.string.settings_export_success)
    val exportErrorMessage = stringResource(R.string.settings_export_error)
    val importSuccessMessage = stringResource(R.string.settings_import_success)
    val importErrorMessage = stringResource(R.string.settings_import_error)

    LaunchedEffect(uiState.exportState) {
        when (val state = uiState.exportState) {
            is BackupOperation.Success -> {
                snackbarHostState.showSnackbar(exportSuccessMessage)
                viewModel.clearExportState()
            }
            is BackupOperation.Error -> {
                snackbarHostState.showSnackbar(exportErrorMessage.format(state.message))
                viewModel.clearExportState()
            }
            else -> Unit
        }
    }

    LaunchedEffect(uiState.importState) {
        when (val state = uiState.importState) {
            is BackupOperation.Success -> {
                snackbarHostState.showSnackbar(importSuccessMessage)
                viewModel.clearImportState()
            }
            is BackupOperation.Error -> {
                snackbarHostState.showSnackbar(importErrorMessage.format(state.message))
                viewModel.clearImportState()
            }
            else -> Unit
        }
    }

    SettingsContent(
        uiState = uiState,
        showImportConfirmDialog = showImportConfirmDialog,
        onExportClick = { exportLauncher.launch(defaultBackupFilename()) },
        onImportClick = { importLauncher.launch(arrayOf("application/json")) },
        onImportConfirm = {
            showImportConfirmDialog = false
            pendingImportUri?.let { viewModel.import(it) }
            pendingImportUri = null
        },
        onImportDismiss = {
            showImportConfirmDialog = false
            pendingImportUri = null
        },
        onSignOutClick = { viewModel.signOut() },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    showImportConfirmDialog: Boolean,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onImportConfirm: () -> Unit,
    onImportDismiss: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = onImportDismiss,
            title = { Text(stringResource(R.string.settings_import_confirm_title)) },
            text = { Text(stringResource(R.string.settings_import_confirm_message)) },
            confirmButton = {
                TextButton(onClick = onImportConfirm) {
                    Text(stringResource(R.string.settings_import_confirm_action))
                }
            },
            dismissButton = {
                TextButton(onClick = onImportDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_settings)) }) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        val isExporting = uiState.exportState is BackupOperation.InProgress
        val isImporting = uiState.importState is BackupOperation.InProgress

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.settings_backup_section_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = onExportClick,
                enabled = !isExporting && !isImporting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    if (isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.settings_export_button))
                }
            }

            OutlinedButton(
                onClick = onImportClick,
                enabled = !isExporting && !isImporting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    if (isImporting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.settings_import_button))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.settings_account_section_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.settings_sign_out_button))
                }
            }
        }
    }
}

private fun defaultBackupFilename(): String {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DAY_OF_MONTH)
    return "espresso_backup_%04d-%02d-%02d.json".format(year, month, day)
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    EspressoAppTheme {
        SettingsContent(
            uiState = SettingsUiState(),
            showImportConfirmDialog = false,
            onExportClick = {},
            onImportClick = {},
            onImportConfirm = {},
            onImportDismiss = {},
            onSignOutClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentInProgressPreview() {
    EspressoAppTheme {
        SettingsContent(
            uiState = SettingsUiState(exportState = BackupOperation.InProgress),
            showImportConfirmDialog = false,
            onExportClick = {},
            onImportClick = {},
            onImportConfirm = {},
            onImportDismiss = {},
            onSignOutClick = {},
        )
    }
}
