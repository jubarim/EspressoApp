package org.juba.espressoapp.ui.coffee.roaster

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.EmptyStateContent
import org.juba.espressoapp.designsystem.toolbar.ContextActionsToolbar
import org.juba.espressoapp.designsystem.toolbar.FloatingNavItem
import org.juba.espressoapp.domain.model.Roaster
import org.juba.espressoapp.ui.main.LocalNavBarPadding
import org.juba.espressoapp.ui.main.LocalSnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoasterListScreen(
    onBack: () -> Unit,
    onRoasterClick: (String) -> Unit,
    onAddRoaster: () -> Unit,
    onEditRoaster: (String) -> Unit,
    onViewShots: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoasterListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val failedMessage = stringResource(R.string.action_failed)

    LaunchedEffect(viewModel, snackbarHostState, failedMessage) {
        viewModel.events.collect { event ->
            when (event) {
                RoasterListEvent.ActionFailed -> snackbarHostState.showSnackbar(failedMessage)
            }
        }
    }

    val successState = uiState as? RoasterListUiState.Success
    var selectedRoasterId by remember { mutableStateOf<String?>(null) }
    var deleteRoasterId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedRoaster = successState?.roasters?.firstOrNull { it.id == selectedRoasterId }

    LaunchedEffect(successState?.roasters) {
        val roasters = successState?.roasters.orEmpty()
        if (selectedRoasterId != null && roasters.none { it.id == selectedRoasterId }) {
            selectedRoasterId = null
        }
        if (deleteRoasterId != null && roasters.none { it.id == deleteRoasterId }) {
            deleteRoasterId = null
        }
    }

    BackHandler(enabled = selectedRoasterId != null) {
        selectedRoasterId = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.coffee_category_roasters)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
        floatingActionButton = {
            val roaster = selectedRoaster
            ContextActionsToolbar(
                expanded = roaster != null,
                items = if (roaster != null) {
                    listOf(
                        FloatingNavItem(Icons.Default.Edit, R.string.cd_edit) {
                            selectedRoasterId = null
                            onEditRoaster(roaster.id)
                        },
                        FloatingNavItem(Icons.Default.Delete, R.string.cd_delete) {
                            deleteRoasterId = roaster.id
                        },
                        FloatingNavItem(Icons.AutoMirrored.Filled.List, R.string.cd_view_shots) {
                            selectedRoasterId = null
                            viewModel.setShotFilter(roaster)
                            onViewShots()
                        },
                    )
                } else {
                    emptyList()
                },
                onFabClick = {
                    if (roaster != null) selectedRoasterId = null else onAddRoaster()
                },
                collapsedFabIcon = Icons.Default.Add,
                collapsedFabContentDescription = stringResource(R.string.cd_add_roaster),
            )
        },
        contentWindowInsets = WindowInsets(bottom = LocalNavBarPadding.current),
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
                    message = stringResource(R.string.roaster_list_empty_message),
                    actionLabel = stringResource(R.string.roaster_add),
                    onAction = onAddRoaster,
                    modifier = Modifier.padding(innerPadding),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { selectedRoasterId = null }
                        },
                    contentPadding = innerPadding,
                ) {
                    items(state.roasters, key = { it.id }) { roaster ->
                        RoasterListItem(
                            roaster = roaster,
                            isSelected = roaster.id == selectedRoasterId,
                            onClick = {
                                if (selectedRoasterId != null) {
                                    selectedRoasterId = null
                                } else {
                                    onRoasterClick(roaster.id)
                                }
                            },
                            onLongClick = { selectedRoasterId = roaster.id },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    deleteRoasterId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteRoasterId = null },
            title = { Text(stringResource(R.string.roaster_delete_dialog_title)) },
            text = { Text(stringResource(R.string.roaster_delete_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    deleteRoasterId = null
                    selectedRoasterId = null
                    viewModel.delete(id)
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteRoasterId = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RoasterListItem(
    roaster: Roaster,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        supportingContent = listOfNotNull(roaster.city, roaster.country)
            .joinToString(", ")
            .ifEmpty { null }
            ?.let { location -> { Text(location) } },
        leadingContent = { RoasterAvatar(roaster) },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Transparent
            },
        ),
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
        ),
    ) {
        Text(roaster.name)
    }
}

@Composable
private fun RoasterAvatar(roaster: Roaster) {
    val imageUri = roaster.imageUri
    if (imageUri != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.roaster_logo_cd, roaster.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
    } else {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = roaster.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
