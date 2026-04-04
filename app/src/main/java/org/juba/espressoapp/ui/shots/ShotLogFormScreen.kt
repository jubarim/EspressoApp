package org.juba.espressoapp.ui.shots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.EspressoTextField
import org.juba.espressoapp.designsystem.PickerField
import org.juba.espressoapp.designsystem.SelectionBottomSheet
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShotLogFormScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShotLogFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ShotLogFormContent(
        uiState = uiState,
        onDismiss = onDismiss,
        onSave = viewModel::save,
        onCoffeeBeanChange = viewModel::onCoffeeBeanChange,
        onGrinderChange = viewModel::onGrinderChange,
        onMachineChange = viewModel::onMachineChange,
        onBasketChange = viewModel::onBasketChange,
        onDoseChange = viewModel::onDoseChange,
        onYieldChange = viewModel::onYieldChange,
        onGrindSettingChange = viewModel::onGrindSettingChange,
        onExtractionTimeChange = viewModel::onExtractionTimeChange,
        onBrewTemperatureChange = viewModel::onBrewTemperatureChange,
        onPreInfusionChange = viewModel::onPreInfusionChange,
        onRatingChange = viewModel::onRatingChange,
        onNotesChange = viewModel::onNotesChange,
        onShotAtChange = viewModel::onShotAtChange,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShotLogFormContent(
    uiState: ShotLogFormUiState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onCoffeeBeanChange: (id: String, label: String) -> Unit,
    onGrinderChange: (id: String, label: String) -> Unit,
    onMachineChange: (id: String, label: String) -> Unit,
    onBasketChange: (id: String, label: String) -> Unit,
    onDoseChange: (String) -> Unit,
    onYieldChange: (String) -> Unit,
    onGrindSettingChange: (String) -> Unit,
    onExtractionTimeChange: (String) -> Unit,
    onBrewTemperatureChange: (String) -> Unit,
    onPreInfusionChange: (String) -> Unit,
    onRatingChange: (Int?) -> Unit,
    onNotesChange: (String) -> Unit,
    onShotAtChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCoffeeBeanSheet by rememberSaveable { mutableStateOf(false) }
    var showGrinderSheet by rememberSaveable { mutableStateOf(false) }
    var showMachineSheet by rememberSaveable { mutableStateOf(false) }
    var showBasketSheet by rememberSaveable { mutableStateOf(false) }
    var showRatingSheet by rememberSaveable { mutableStateOf(false) }
    var showShotDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onDismiss()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.coffeeBeanId.isEmpty()) stringResource(R.string.shot_log_form_add)
                        else stringResource(R.string.shot_log_form_edit),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_close))
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
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
            PickerField(
                label = stringResource(R.string.field_coffee_bean),
                selectedLabel = uiState.coffeeBeanLabel,
                onClick = { showCoffeeBeanSheet = true },
                errorMessage = uiState.coffeeBeanError,
            )
            PickerField(
                label = stringResource(R.string.field_grinder),
                selectedLabel = uiState.grinderLabel,
                onClick = { showGrinderSheet = true },
                errorMessage = uiState.grinderError,
            )
            PickerField(
                label = stringResource(R.string.field_machine),
                selectedLabel = uiState.machineLabel,
                onClick = { showMachineSheet = true },
                errorMessage = uiState.machineError,
            )
            PickerField(
                label = stringResource(R.string.field_basket),
                selectedLabel = uiState.basketLabel,
                onClick = { showBasketSheet = true },
                errorMessage = uiState.basketError,
            )
            EspressoTextField(
                value = uiState.dose,
                onValueChange = onDoseChange,
                label = stringResource(R.string.field_dose_grams),
                errorMessage = uiState.doseError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            EspressoTextField(
                value = uiState.yield,
                onValueChange = onYieldChange,
                label = stringResource(R.string.field_yield_grams),
                errorMessage = uiState.yieldError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            EspressoTextField(
                value = uiState.grindSetting,
                onValueChange = onGrindSettingChange,
                label = stringResource(R.string.field_grind_setting),
            )
            EspressoTextField(
                value = uiState.extractionTimeSeconds,
                onValueChange = onExtractionTimeChange,
                label = stringResource(R.string.field_extraction_time_seconds),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            EspressoTextField(
                value = uiState.brewTemperatureCelsius,
                onValueChange = onBrewTemperatureChange,
                label = stringResource(R.string.field_brew_temperature_celsius),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            EspressoTextField(
                value = uiState.preInfusionSeconds,
                onValueChange = onPreInfusionChange,
                label = stringResource(R.string.field_pre_infusion_seconds),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            val ratingLabel = uiState.rating?.let {
                ShotLogFormViewModel.ratingOptions.find { opt -> opt.key == it.toString() }?.label
            }
            PickerField(
                label = stringResource(R.string.field_rating),
                selectedLabel = ratingLabel,
                onClick = { showRatingSheet = true },
            )
            val shotDateDisplay = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(uiState.shotAt))
            PickerField(
                label = stringResource(R.string.field_shot_date),
                selectedLabel = shotDateDisplay,
                onClick = { showShotDatePicker = true },
            )
            EspressoTextField(
                value = uiState.notes,
                onValueChange = onNotesChange,
                label = stringResource(R.string.field_notes),
                singleLine = false,
            )
        }
    }

    if (showCoffeeBeanSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_coffee_bean),
            options = uiState.coffeeBeanOptions,
            selectedKeys = setOf(uiState.coffeeBeanId).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys ->
                keys.firstOrNull()?.let { id ->
                    onCoffeeBeanChange(id, uiState.coffeeBeanOptions.find { it.key == id }?.label ?: "")
                }
            },
            onDismiss = { showCoffeeBeanSheet = false },
            multiSelect = false,
        )
    }

    if (showGrinderSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_grinder),
            options = uiState.grinderOptions,
            selectedKeys = setOf(uiState.grinderId).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys ->
                keys.firstOrNull()?.let { id ->
                    onGrinderChange(id, uiState.grinderOptions.find { it.key == id }?.label ?: "")
                }
            },
            onDismiss = { showGrinderSheet = false },
            multiSelect = false,
        )
    }

    if (showMachineSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_machine),
            options = uiState.machineOptions,
            selectedKeys = setOf(uiState.machineId).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys ->
                keys.firstOrNull()?.let { id ->
                    onMachineChange(id, uiState.machineOptions.find { it.key == id }?.label ?: "")
                }
            },
            onDismiss = { showMachineSheet = false },
            multiSelect = false,
        )
    }

    if (showBasketSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_basket),
            options = uiState.basketOptions,
            selectedKeys = setOf(uiState.basketId).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys ->
                keys.firstOrNull()?.let { id ->
                    onBasketChange(id, uiState.basketOptions.find { it.key == id }?.label ?: "")
                }
            },
            onDismiss = { showBasketSheet = false },
            multiSelect = false,
        )
    }

    if (showRatingSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_rating),
            options = ShotLogFormViewModel.ratingOptions,
            selectedKeys = uiState.rating?.let { setOf(it.toString()) } ?: emptySet(),
            onConfirm = { keys -> onRatingChange(keys.firstOrNull()?.toIntOrNull()) },
            onDismiss = { showRatingSheet = false },
            multiSelect = false,
        )
    }

    if (showShotDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.shotAt)
        DatePickerDialog(
            onDismissRequest = { showShotDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onShotAtChange(it) }
                    showShotDatePicker = false
                }) { Text(stringResource(R.string.picker_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showShotDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true, name = "Empty form")
@Composable
private fun ShotLogFormContentEmptyPreview() {
    EspressoAppTheme {
        ShotLogFormContent(
            uiState = ShotLogFormUiState(),
            onDismiss = {},
            onSave = {},
            onCoffeeBeanChange = { _, _ -> },
            onGrinderChange = { _, _ -> },
            onMachineChange = { _, _ -> },
            onBasketChange = { _, _ -> },
            onDoseChange = {},
            onYieldChange = {},
            onGrindSettingChange = {},
            onExtractionTimeChange = {},
            onBrewTemperatureChange = {},
            onPreInfusionChange = {},
            onRatingChange = {},
            onNotesChange = {},
            onShotAtChange = {},
        )
    }
}

@Preview(showBackground = true, name = "Filled form with errors")
@Composable
private fun ShotLogFormContentErrorsPreview() {
    EspressoAppTheme {
        ShotLogFormContent(
            uiState = ShotLogFormUiState(
                coffeeBeanLabel = "Ethiopia Yirgacheffe · Nordic",
                coffeeBeanId = "b1",
                dose = "18,5",
                doseError = "Valid dose is required",
                yieldError = "Valid yield is required",
                grindSetting = "2.5",
                rating = 4,
            ),
            onDismiss = {},
            onSave = {},
            onCoffeeBeanChange = { _, _ -> },
            onGrinderChange = { _, _ -> },
            onMachineChange = { _, _ -> },
            onBasketChange = { _, _ -> },
            onDoseChange = {},
            onYieldChange = {},
            onGrindSettingChange = {},
            onExtractionTimeChange = {},
            onBrewTemperatureChange = {},
            onPreInfusionChange = {},
            onRatingChange = {},
            onNotesChange = {},
            onShotAtChange = {},
        )
    }
}
