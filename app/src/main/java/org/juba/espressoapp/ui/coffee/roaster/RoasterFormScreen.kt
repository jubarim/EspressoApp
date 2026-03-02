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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                title = { Text(if (viewModel.uiState.value.name.isEmpty()) "Add Roaster" else "Edit Roaster") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = viewModel::save,
                        enabled = !uiState.isSaving,
                    ) { Text("Save") }
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
                label = "Name *",
                errorMessage = uiState.nameError,
            )
            EspressoTextField(
                value = uiState.country,
                onValueChange = viewModel::onCountryChange,
                label = "Country",
            )
            EspressoTextField(
                value = uiState.website,
                onValueChange = viewModel::onWebsiteChange,
                label = "Website",
            )
            EspressoTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Notes",
                singleLine = false,
            )
        }
    }
}
