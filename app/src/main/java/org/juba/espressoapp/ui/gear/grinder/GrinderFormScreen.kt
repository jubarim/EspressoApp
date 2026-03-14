package org.juba.espressoapp.ui.gear.grinder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.EspressoTextField
import org.juba.espressoapp.designsystem.PickerField
import org.juba.espressoapp.designsystem.SelectionBottomSheet
import org.juba.espressoapp.domain.model.BurrType
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrinderFormScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GrinderFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showBurrTypeSheet by rememberSaveable { mutableStateOf(false) }
    var showPurchaseDatePicker by rememberSaveable { mutableStateOf(false) }
    var showBurrInstallDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onDismiss()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (uiState.brand.isEmpty()) {
                        stringResource(R.string.grinder_add)
                    } else {
                        stringResource(R.string.grinder_edit)
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_close))
                    }
                },
                actions = {
                    TextButton(
                        onClick = viewModel::save,
                        enabled = !uiState.isSaving,
                    ) { Text(stringResource(R.string.action_save)) }
                },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EspressoTextField(
                value = uiState.brand,
                onValueChange = viewModel::onBrandChange,
                label = stringResource(R.string.field_brand),
                errorMessage = uiState.brandError,
            )
            EspressoTextField(
                value = uiState.model,
                onValueChange = viewModel::onModelChange,
                label = stringResource(R.string.field_model),
                errorMessage = uiState.modelError,
            )
            PickerField(
                label = stringResource(R.string.field_burr_type),
                selectedLabel = uiState.burrType.ifBlank { null },
                onClick = { showBurrTypeSheet = true },
            )
            EspressoTextField(
                value = uiState.burrSize,
                onValueChange = viewModel::onBurrSizeChange,
                label = stringResource(R.string.field_burr_size),
            )
            val purchaseDateDisplay = uiState.purchaseDate?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(it))
            }
            PickerField(
                label = stringResource(R.string.field_purchase_date),
                selectedLabel = purchaseDateDisplay,
                onClick = { showPurchaseDatePicker = true },
            )
            val burrInstallDateDisplay = uiState.burrInstallDate?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(it))
            }
            PickerField(
                label = stringResource(R.string.field_burr_install_date),
                selectedLabel = burrInstallDateDisplay,
                onClick = { showBurrInstallDatePicker = true },
            )
            EspressoTextField(
                value = uiState.imageUrl,
                onValueChange = viewModel::onImageUrlChange,
                label = stringResource(R.string.field_image_url),
            )
            EspressoTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = stringResource(R.string.field_notes),
                singleLine = false,
            )
        }
    }

    if (showBurrTypeSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_burr_type),
            options = BurrType.options,
            selectedKeys = setOf(uiState.burrType).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys -> viewModel.onBurrTypeChange(keys.firstOrNull() ?: "") },
            onDismiss = { showBurrTypeSheet = false },
            multiSelect = false,
        )
    }

    if (showPurchaseDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.purchaseDate,
        )
        DatePickerDialog(
            onDismissRequest = { showPurchaseDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onPurchaseDateChange(datePickerState.selectedDateMillis)
                    showPurchaseDatePicker = false
                }) { Text(stringResource(R.string.picker_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showPurchaseDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showBurrInstallDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.burrInstallDate,
        )
        DatePickerDialog(
            onDismissRequest = { showBurrInstallDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onBurrInstallDateChange(datePickerState.selectedDateMillis)
                    showBurrInstallDatePicker = false
                }) { Text(stringResource(R.string.picker_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showBurrInstallDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GrinderFormScreenPreview() {
    EspressoAppTheme {
        GrinderFormScreen(onDismiss = {})
    }
}
