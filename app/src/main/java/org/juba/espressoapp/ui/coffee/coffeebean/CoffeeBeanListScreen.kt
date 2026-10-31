package org.juba.espressoapp.ui.coffee.coffeebean

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
import androidx.compose.material.icons.filled.ContentCopy
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
import org.juba.espressoapp.designsystem.QuickCopyDialog
import org.juba.espressoapp.designsystem.toolbar.ContextActionsToolbar
import org.juba.espressoapp.designsystem.toolbar.FloatingNavItem
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.ui.main.LocalNavBarPadding
import org.juba.espressoapp.ui.main.LocalSnackbarHostState

@Composable
fun CoffeeBeanListScreen(
    onBack: () -> Unit,
    onBeanClick: (String) -> Unit,
    onAddBean: () -> Unit,
    onEditBean: (String) -> Unit,
    onCopyCustomize: (String) -> Unit,
    onViewShots: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoffeeBeanListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val copiedMessage = stringResource(R.string.copy_bean_success)
    val failedMessage = stringResource(R.string.action_failed)

    LaunchedEffect(viewModel, snackbarHostState, copiedMessage, failedMessage) {
        viewModel.events.collect { event ->
            when (event) {
                CoffeeBeanListEvent.Copied -> snackbarHostState.showSnackbar(copiedMessage)
                CoffeeBeanListEvent.ActionFailed -> snackbarHostState.showSnackbar(failedMessage)
            }
        }
    }

    CoffeeBeanListContent(
        uiState = uiState,
        onBack = onBack,
        onBeanClick = onBeanClick,
        onAddBean = onAddBean,
        onEditBean = onEditBean,
        onCopyCustomize = onCopyCustomize,
        onViewShots = onViewShots,
        onDeleteBean = viewModel::deleteBean,
        onCopyBean = viewModel::copyBean,
        onSetShotFilter = viewModel::setShotFilter,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CoffeeBeanListContent(
    uiState: CoffeeBeanListUiState,
    onBack: () -> Unit,
    onBeanClick: (String) -> Unit,
    onAddBean: () -> Unit,
    onEditBean: (String) -> Unit,
    onCopyCustomize: (String) -> Unit,
    onViewShots: () -> Unit,
    onDeleteBean: (String) -> Unit,
    onCopyBean: (CoffeeBean, Long?) -> Unit,
    onSetShotFilter: (CoffeeBean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val successState = uiState as? CoffeeBeanListUiState.Success
    var selectedBeanId by remember { mutableStateOf<String?>(null) }
    var deleteBeanId by rememberSaveable { mutableStateOf<String?>(null) }
    var copyBeanId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedBean = successState?.beans?.firstOrNull { it.id == selectedBeanId }
    val copyBean = successState?.beans?.firstOrNull { it.id == copyBeanId }

    LaunchedEffect(successState?.beans) {
        val beans = successState?.beans.orEmpty()
        if (selectedBeanId != null && beans.none { it.id == selectedBeanId }) selectedBeanId = null
        if (deleteBeanId != null && beans.none { it.id == deleteBeanId }) deleteBeanId = null
        if (copyBeanId != null && beans.none { it.id == copyBeanId }) copyBeanId = null
    }

    BackHandler(enabled = selectedBeanId != null) {
        selectedBeanId = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.coffee_category_coffee_beans)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
        floatingActionButton = {
            val bean = selectedBean
            ContextActionsToolbar(
                expanded = bean != null,
                items = if (bean != null) {
                    listOf(
                        FloatingNavItem(Icons.Default.Edit, R.string.cd_edit) {
                            selectedBeanId = null
                            onEditBean(bean.id)
                        },
                        FloatingNavItem(Icons.Default.Delete, R.string.cd_delete) {
                            deleteBeanId = bean.id
                        },
                        FloatingNavItem(Icons.Default.ContentCopy, R.string.cd_copy) {
                            copyBeanId = bean.id
                        },
                        FloatingNavItem(Icons.AutoMirrored.Filled.List, R.string.cd_view_shots) {
                            selectedBeanId = null
                            onSetShotFilter(bean)
                            onViewShots()
                        },
                    )
                } else {
                    emptyList()
                },
                onFabClick = {
                    if (bean != null) selectedBeanId = null else onAddBean()
                },
                collapsedFabIcon = Icons.Default.Add,
                collapsedFabContentDescription = stringResource(R.string.cd_add_coffee_bean),
            )
        },
        contentWindowInsets = WindowInsets(bottom = LocalNavBarPadding.current),
        modifier = modifier,
    ) { innerPadding ->
        when (val state = uiState) {
            is CoffeeBeanListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is CoffeeBeanListUiState.Error -> EmptyStateContent(
                message = state.message,
                modifier = Modifier.padding(innerPadding),
            )

            is CoffeeBeanListUiState.Success -> if (state.beans.isEmpty()) {
                EmptyStateContent(
                    message = stringResource(R.string.coffee_bean_list_empty_message),
                    actionLabel = stringResource(R.string.coffee_bean_add),
                    onAction = onAddBean,
                    modifier = Modifier.padding(innerPadding),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { selectedBeanId = null }
                        },
                    contentPadding = innerPadding,
                ) {
                    items(state.beans, key = { it.id }) { bean ->
                        CoffeeBeanListItem(
                            bean = bean,
                            isSelected = bean.id == selectedBeanId,
                            onClick = {
                                if (selectedBeanId != null) {
                                    selectedBeanId = null
                                } else {
                                    onBeanClick(bean.id)
                                }
                            },
                            onLongClick = { selectedBeanId = bean.id },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    deleteBeanId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteBeanId = null },
            title = { Text(stringResource(R.string.coffee_bean_delete_dialog_title)) },
            text = { Text(stringResource(R.string.coffee_bean_delete_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    deleteBeanId = null
                    selectedBeanId = null
                    onDeleteBean(id)
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteBeanId = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    copyBean?.let { bean ->
        QuickCopyDialog(
            title = stringResource(R.string.copy_bean_dialog_title),
            dateFieldLabel = stringResource(R.string.copy_bean_dialog_roast_date_label),
            noDatePlaceholder = stringResource(R.string.copy_bean_dialog_no_date),
            initialDate = bean.roastDate,
            defaultPickerDate = bean.roastDate,
            onDismiss = { copyBeanId = null },
            onCustomize = {
                copyBeanId = null
                selectedBeanId = null
                onCopyCustomize(bean.id)
            },
            onCreate = { newDate ->
                copyBeanId = null
                selectedBeanId = null
                onCopyBean(bean, newDate)
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoffeeBeanListItem(
    bean: CoffeeBean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        supportingContent = bean.roasterName
            ?.let { roaster -> { Text(roaster) } },
        leadingContent = { CoffeeBeanAvatar(bean) },
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
        Text(bean.name)
    }
}

@Composable
private fun CoffeeBeanAvatar(bean: CoffeeBean) {
    val imageUri = bean.imageUri
    if (imageUri != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.coffee_bean_image_cd, bean.name),
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
                text = bean.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
