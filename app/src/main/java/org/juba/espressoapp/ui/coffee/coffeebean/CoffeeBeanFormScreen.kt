package org.juba.espressoapp.ui.coffee.coffeebean

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.EspressoTextField
import org.juba.espressoapp.designsystem.PickerField
import org.juba.espressoapp.designsystem.SelectionBottomSheet
import org.juba.espressoapp.designsystem.SelectionOption
import org.juba.espressoapp.domain.model.BeanProcess
import org.juba.espressoapp.domain.model.RoastLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeBeanFormScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoffeeBeanFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showRoasterSheet by rememberSaveable { mutableStateOf(false) }
    var showProcessSheet by rememberSaveable { mutableStateOf(false) }
    var showRoastLevelSheet by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onDismiss()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (uiState.name.isEmpty()) {
                        stringResource(R.string.coffee_bean_add)
                    } else {
                        stringResource(R.string.coffee_bean_edit)
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
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = stringResource(R.string.field_name),
                errorMessage = uiState.nameError,
            )
            PickerField(
                label = stringResource(R.string.field_roaster),
                selectedLabel = uiState.roasterName.ifBlank { null },
                onClick = { showRoasterSheet = true },
            )
            uiState.roasterError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            EspressoTextField(
                value = uiState.origin,
                onValueChange = viewModel::onOriginChange,
                label = stringResource(R.string.field_origin),
            )
            PickerField(
                label = stringResource(R.string.field_process),
                selectedLabel = uiState.process.ifBlank { null },
                onClick = { showProcessSheet = true },
            )
            PickerField(
                label = stringResource(R.string.field_roast_level),
                selectedLabel = uiState.roastLevel.ifBlank { null },
                onClick = { showRoastLevelSheet = true },
            )
            val roastDateDisplay = remember(uiState.roastDate) {
                uiState.roastDate?.let {
                    SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(it))
                }
            }
            PickerField(
                label = stringResource(R.string.field_roast_date),
                selectedLabel = roastDateDisplay,
                onClick = { showDatePicker = true },
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

    if (showRoasterSheet) {
        val roasterOptions = remember(uiState.roasters) {
            uiState.roasters.map { SelectionOption(it.id, it.name) }
        }
        SelectionBottomSheet(
            title = stringResource(R.string.field_roaster),
            options = roasterOptions,
            selectedKeys = setOf(uiState.roasterId).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys ->
                val selectedId = keys.firstOrNull() ?: ""
                val selectedName = uiState.roasters.find { it.id == selectedId }?.name ?: ""
                viewModel.onRoasterChange(selectedId, selectedName)
            },
            onDismiss = { showRoasterSheet = false },
            multiSelect = false,
        )
    }

    if (showProcessSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_process),
            options = BeanProcess.options,
            selectedKeys = setOf(uiState.process).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys -> viewModel.onProcessChange(keys.firstOrNull() ?: "") },
            onDismiss = { showProcessSheet = false },
            multiSelect = false,
        )
    }

    if (showRoastLevelSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_roast_level),
            options = RoastLevel.options,
            selectedKeys = setOf(uiState.roastLevel).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys -> viewModel.onRoastLevelChange(keys.firstOrNull() ?: "") },
            onDismiss = { showRoastLevelSheet = false },
            multiSelect = false,
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.roastDate,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onRoastDateChange(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text(stringResource(R.string.picker_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
