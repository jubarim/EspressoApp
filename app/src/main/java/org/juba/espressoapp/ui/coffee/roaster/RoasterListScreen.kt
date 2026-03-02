package org.juba.espressoapp.ui.coffee.roaster

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.ui.designsystem.EmptyStateContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoasterListScreen(
    onBack: () -> Unit,
    onRoasterClick: (String) -> Unit,
    onAddRoaster: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoasterListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roasters") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRoaster) {
                Icon(Icons.Default.Add, contentDescription = "Add roaster")
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        when (val state = uiState) {
            is RoasterListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is RoasterListUiState.Error -> EmptyStateContent(
                message = state.message,
                modifier = Modifier.padding(innerPadding),
            )

            is RoasterListUiState.Success -> if (state.roasters.isEmpty()) {
                EmptyStateContent(
                    message = "No roasters yet.\nTap + to add your first one.",
                    actionLabel = "Add Roaster",
                    onAction = onAddRoaster,
                    modifier = Modifier.padding(innerPadding),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding,
                ) {
                    items(state.roasters, key = { it.id }) { roaster ->
                        ListItem(
                            headlineContent = { Text(roaster.name) },
                            supportingContent = roaster.country?.let { { Text(it) } },
                            modifier = Modifier.clickable { onRoasterClick(roaster.id) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
