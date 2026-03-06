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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.designsystem.EspressoTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoasterFormScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoasterFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            EspressoTextField(
                value = uiState.country,
                onValueChange = viewModel::onCountryChange,
                label = stringResource(R.string.field_country),
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
}
