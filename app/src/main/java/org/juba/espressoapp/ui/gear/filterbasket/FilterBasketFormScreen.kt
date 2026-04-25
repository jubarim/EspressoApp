package org.juba.espressoapp.ui.gear.filterbasket

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
import org.juba.espressoapp.domain.model.FilterBasketDiameter
import org.juba.espressoapp.domain.model.FilterBasketType
import org.juba.espressoapp.extensions.epochToLocalTimeZoneConvertor
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FilterBasketFormScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FilterBasketFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showTypeSheet by rememberSaveable { mutableStateOf(false) }
    var showDiameterSheet by rememberSaveable { mutableStateOf(false) }
    var showPurchaseDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onDismiss()
    }

    FilterBasketFormContent(
        uiState = uiState,
        onDismiss = onDismiss,
        onSave = viewModel::save,
        onBrandChange = viewModel::onBrandChange,
        onModelChange = viewModel::onModelChange,
        onSizeGramsChange = viewModel::onSizeGramsChange,
        onImageUrlChange = viewModel::onImageUrlChange,
        onNotesChange = viewModel::onNotesChange,
        onShowTypeSheet = { showTypeSheet = true },
        onShowDiameterSheet = { showDiameterSheet = true },
        onShowPurchaseDatePicker = { showPurchaseDatePicker = true },
        modifier = modifier,
    )

    if (showTypeSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_basket_type),
            options = FilterBasketType.options,
            selectedKeys = setOf(uiState.type).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys -> viewModel.onTypeChange(keys.firstOrNull() ?: "") },
            onDismiss = { showTypeSheet = false },
            multiSelect = false,
        )
    }

    if (showDiameterSheet) {
        SelectionBottomSheet(
            title = stringResource(R.string.field_diameter),
            options = FilterBasketDiameter.options,
            selectedKeys = setOf(uiState.diameter).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys -> viewModel.onDiameterChange(keys.firstOrNull() ?: "") },
            onDismiss = { showDiameterSheet = false },
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
                    viewModel.onPurchaseDateChange(datePickerState.selectedDateMillis?.epochToLocalTimeZoneConvertor())
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBasketFormContent(
    uiState: FilterBasketFormUiState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onSizeGramsChange: (String) -> Unit,
    onImageUrlChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onShowTypeSheet: () -> Unit,
    onShowDiameterSheet: () -> Unit,
    onShowPurchaseDatePicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (uiState.brand.isEmpty()) {
                        stringResource(R.string.filter_basket_add)
                    } else {
                        stringResource(R.string.filter_basket_edit)
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
            EspressoTextField(
                value = uiState.brand,
                onValueChange = onBrandChange,
                label = stringResource(R.string.field_brand),
                errorMessage = uiState.brandError,
            )
            EspressoTextField(
                value = uiState.model,
                onValueChange = onModelChange,
                label = stringResource(R.string.field_model),
            )
            EspressoTextField(
                value = uiState.sizeGrams,
                onValueChange = onSizeGramsChange,
                label = stringResource(R.string.field_size),
            )
            PickerField(
                label = stringResource(R.string.field_basket_type),
                selectedLabel = uiState.type.ifBlank { null },
                onClick = onShowTypeSheet,
            )
            PickerField(
                label = stringResource(R.string.field_diameter),
                selectedLabel = uiState.diameter.ifBlank { null },
                onClick = onShowDiameterSheet,
            )
            val purchaseDateDisplay = uiState.purchaseDate?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(it))
            }
            PickerField(
                label = stringResource(R.string.field_purchase_date),
                selectedLabel = purchaseDateDisplay,
                onClick = onShowPurchaseDatePicker,
            )
            EspressoTextField(
                value = uiState.imageUrl,
                onValueChange = onImageUrlChange,
                label = stringResource(R.string.field_image_url),
            )
            EspressoTextField(
                value = uiState.notes,
                onValueChange = onNotesChange,
                label = stringResource(R.string.field_notes),
                singleLine = false,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterBasketFormScreenPreview() {
    EspressoAppTheme {
        FilterBasketFormContent(
            uiState = FilterBasketFormUiState(),
            onDismiss = {},
            onSave = {},
            onBrandChange = {},
            onModelChange = {},
            onSizeGramsChange = {},
            onImageUrlChange = {},
            onNotesChange = {},
            onShowTypeSheet = {},
            onShowDiameterSheet = {},
            onShowPurchaseDatePicker = {},
        )
    }
}
