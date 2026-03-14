package org.juba.espressoapp.ui.coffee.roaster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import org.juba.espressoapp.domain.model.Countries
import org.juba.espressoapp.designsystem.EspressoTextField
import org.juba.espressoapp.designsystem.PickerField
import org.juba.espressoapp.designsystem.SelectionBottomSheet
import org.juba.espressoapp.designsystem.SelectionOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoasterFormScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoasterFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showCountrySheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onDismiss()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (viewModel.uiState.value.name.isEmpty()) {
                        stringResource(R.string.roaster_add)
                    } else {
                        stringResource(R.string.roaster_edit)
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
            val selectedCountryLabel = remember(uiState.country) {
                Countries.all.find { it.code == uiState.country }?.name
            }
            PickerField(
                label = stringResource(R.string.field_country),
                selectedLabel = selectedCountryLabel,
                onClick = { showCountrySheet = true },
            )
            EspressoTextField(
                value = uiState.city,
                onValueChange = viewModel::onCityChange,
                label = stringResource(R.string.field_city),
            )
            EspressoTextField(
                value = uiState.website,
                onValueChange = viewModel::onWebsiteChange,
                label = stringResource(R.string.field_website),
            )
            EspressoTextField(
                value = uiState.logoUrl,
                onValueChange = viewModel::onLogoUrlChange,
                label = stringResource(R.string.field_logo_url),
            )
            EspressoTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = stringResource(R.string.field_notes),
                singleLine = false,
            )
        }
    }

    if (showCountrySheet) {
        val countryOptions = remember { Countries.all.map { SelectionOption(it.code, it.name) } }
        SelectionBottomSheet(
            title = stringResource(R.string.field_country),
            options = countryOptions,
            selectedKeys = setOf(uiState.country).filter { it.isNotBlank() }.toSet(),
            onConfirm = { keys -> viewModel.onCountryChange(keys.firstOrNull() ?: "") },
            onDismiss = { showCountrySheet = false },
            multiSelect = false,
            skipPartiallyExpanded = true,
        )
    }
}
