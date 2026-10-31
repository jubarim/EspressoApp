package org.juba.espressoapp.ui.shots

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.ActiveFilterChip
import org.juba.espressoapp.designsystem.EmptyStateContent
import org.juba.espressoapp.designsystem.QuickCopyDialog
import org.juba.espressoapp.designsystem.StarRating
import org.juba.espressoapp.designsystem.toolbar.ContextActionsToolbar
import org.juba.espressoapp.designsystem.toolbar.FloatingNavItem
import org.juba.espressoapp.domain.model.FilterBasketSize
import org.juba.espressoapp.domain.model.ShotLog
import org.juba.espressoapp.ui.filter.ShotFilter
import org.juba.espressoapp.ui.main.LocalNavBarPadding
import org.juba.espressoapp.ui.main.LocalSnackbarHostState
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import org.juba.espressoapp.ui.theme.LocalBasketBadgeColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShotsScreen(
    onShotClick: (String) -> Unit,
    onAddShot: () -> Unit,
    onEditShot: (String) -> Unit,
    onCopyCustomize: (String) -> Unit,
    onNavigateToGear: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShotLogListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val copiedMessage = stringResource(R.string.copy_shot_success)
    val failedMessage = stringResource(R.string.action_failed)

    LaunchedEffect(viewModel, snackbarHostState, copiedMessage, failedMessage) {
        viewModel.events.collect { event ->
            when (event) {
                ShotListEvent.Copied -> snackbarHostState.showSnackbar(copiedMessage)
                ShotListEvent.ActionFailed -> snackbarHostState.showSnackbar(failedMessage)
            }
        }
    }

    ShotsScreenContent(
        uiState = uiState,
        onShotClick = onShotClick,
        onAddShot = onAddShot,
        onEditShot = onEditShot,
        onCopyCustomize = onCopyCustomize,
        onNavigateToGear = onNavigateToGear,
        onDeleteShot = viewModel::deleteShot,
        onCopyShot = viewModel::copyShot,
        onFilterByCoffee = viewModel::setFilter,
        onClearFilter = viewModel::clearFilter,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShotsScreenContent(
    uiState: ShotLogListUiState,
    onShotClick: (String) -> Unit,
    onAddShot: () -> Unit,
    onEditShot: (String) -> Unit,
    onCopyCustomize: (String) -> Unit,
    onNavigateToGear: () -> Unit,
    onDeleteShot: (String) -> Unit,
    onCopyShot: (ShotLog, Long) -> Unit,
    onFilterByCoffee: (ShotFilter) -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val successState = uiState as? ShotLogListUiState.Success
    val activeFilter = successState?.activeFilter
    val unknownBeanLabel = stringResource(R.string.shot_log_unknown_bean)
    val filterContentDescription = activeFilter?.let {
        stringResource(R.string.shot_filter_active_cd, it.label)
    }

    var selectedShotId by remember { mutableStateOf<String?>(null) }
    var deleteShotId by rememberSaveable { mutableStateOf<String?>(null) }
    var copyShotId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedShot = successState?.shots?.firstOrNull { it.id == selectedShotId }
    val copyShot = successState?.shots?.firstOrNull { it.id == copyShotId }

    LaunchedEffect(successState?.shots) {
        val shots = successState?.shots.orEmpty()
        if (selectedShotId != null && shots.none { it.id == selectedShotId }) selectedShotId = null
        if (deleteShotId != null && shots.none { it.id == deleteShotId }) deleteShotId = null
        if (copyShotId != null && shots.none { it.id == copyShotId }) copyShotId = null
    }

    BackHandler(enabled = selectedShotId != null || activeFilter != null) {
        if (selectedShotId != null) {
            selectedShotId = null
        } else {
            onClearFilter()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text(stringResource(R.string.tab_shots)) })
                AnimatedVisibility(visible = activeFilter != null) {
                    if (activeFilter != null && filterContentDescription != null) {
                        ActiveFilterChip(
                            label = activeFilter.label,
                            contentDescription = filterContentDescription,
                            onClear = onClearFilter,
                            icon = when (activeFilter) {
                                is ShotFilter.CoffeeBean -> Icons.Default.Coffee
                                is ShotFilter.Roaster -> Icons.Default.Storefront
                            },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val shot = selectedShot
            if (successState?.canAddShot == true) {
                ContextActionsToolbar(
                    expanded = shot != null,
                    items = if (shot != null) {
                        listOf(
                            FloatingNavItem(Icons.Default.Edit, R.string.cd_edit) {
                                selectedShotId = null
                                onEditShot(shot.id)
                            },
                            FloatingNavItem(Icons.Default.Delete, R.string.cd_delete) {
                                deleteShotId = shot.id
                            },
                            FloatingNavItem(Icons.Default.ContentCopy, R.string.cd_copy) {
                                copyShotId = shot.id
                            },
                            FloatingNavItem(Icons.Default.FilterAlt, R.string.cd_filter_by_coffee) {
                                selectedShotId = null
                                onFilterByCoffee(
                                    ShotFilter.CoffeeBean(
                                        coffeeBeanId = shot.coffeeBeanId,
                                        label = shot.coffeeBeanName ?: unknownBeanLabel,
                                    ),
                                )
                            },
                        )
                    } else {
                        emptyList()
                    },
                    onFabClick = {
                        if (shot != null) selectedShotId = null else onAddShot()
                    },
                    collapsedFabIcon = Icons.Default.Add,
                    collapsedFabContentDescription = stringResource(R.string.cd_add_shot),
                )
            }
        },
        contentWindowInsets = WindowInsets(bottom = LocalNavBarPadding.current),
        modifier = modifier,
    ) { innerPadding ->
        when (uiState) {
            is ShotLogListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is ShotLogListUiState.Error -> EmptyStateContent(
                message = uiState.message,
                modifier = Modifier.padding(innerPadding),
            )

            is ShotLogListUiState.Success -> when {
                !uiState.canAddShot -> EmptyStateContent(
                    message = stringResource(R.string.shot_log_list_empty_no_gear),
                    actionLabel = stringResource(R.string.go_to_gear),
                    onAction = onNavigateToGear,
                    modifier = Modifier.padding(innerPadding),
                )

                uiState.totalShotCount == 0 -> EmptyStateContent(
                    message = stringResource(R.string.shot_log_list_empty_no_shots),
                    actionLabel = stringResource(R.string.shot_log_add),
                    onAction = onAddShot,
                    modifier = Modifier.padding(innerPadding),
                )

                uiState.shots.isEmpty() -> EmptyStateContent(
                    message = stringResource(R.string.shot_log_list_empty_filtered),
                    actionLabel = stringResource(R.string.shot_filter_clear),
                    onAction = onClearFilter,
                    modifier = Modifier.padding(innerPadding),
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { selectedShotId = null }
                        },
                    contentPadding = innerPadding,
                ) {
                    items(uiState.shots, key = { it.id }) { shot ->
                        ShotListItem(
                            shot = shot,
                            isSelected = shot.id == selectedShotId,
                            onClick = {
                                if (selectedShotId != null) {
                                    selectedShotId = null
                                } else {
                                    onShotClick(shot.id)
                                }
                            },
                            onLongClick = { selectedShotId = shot.id },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    deleteShotId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteShotId = null },
            title = { Text(stringResource(R.string.shot_log_delete_dialog_title)) },
            text = { Text(stringResource(R.string.shot_log_delete_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    deleteShotId = null
                    selectedShotId = null
                    onDeleteShot(id)
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteShotId = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    copyShot?.let { shot ->
        QuickCopyDialog(
            title = stringResource(R.string.copy_shot_dialog_title),
            dateFieldLabel = stringResource(R.string.copy_shot_dialog_date_label),
            noDatePlaceholder = "",
            initialDate = null,
            defaultPickerDate = System.currentTimeMillis(),
            onDismiss = { copyShotId = null },
            onCustomize = {
                copyShotId = null
                selectedShotId = null
                onCopyCustomize(shot.id)
            },
            onCreate = { newDate ->
                copyShotId = null
                selectedShotId = null
                onCopyShot(shot, newDate ?: System.currentTimeMillis())
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShotListItem(
    shot: ShotLog,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        supportingContent = {
            val parts = buildList {
                add("%.1fg → %.1fg".format(shot.doseGrams, shot.yieldGrams))
                shot.extractionTimeSeconds?.let { add("${it}s") }
            }
            Text(parts.joinToString(" · "))
        },
        leadingContent = { RatioBadge(ratio = shot.ratio, basketSizeClass = shot.basketSizeClass) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatShotDate(shot.shotAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                shot.rating?.let { rating ->
                    StarRating(
                        rating = rating,
                        starSize = 12.dp,
                        spacing = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
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
        Text(shot.coffeeBeanName ?: stringResource(R.string.shot_log_unknown_bean))
    }
}

@Composable
private fun RatioBadge(
    ratio: Double,
    basketSizeClass: String?,
    modifier: Modifier = Modifier,
) {
    val badgeColors = LocalBasketBadgeColors.current
    val (containerColor, contentColor) = badgeColors.colorPairFor(basketSizeClass)
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "1:%.1f".format(ratio),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
        }
    }
}

private fun formatShotDate(timestampMs: Long): String =
    SimpleDateFormat("MMM d", Locale.US).format(Date(timestampMs))

@Preview(showBackground = true, name = "No gear set up")
@Composable
private fun ShotsScreenNoGearPreview() {
    EspressoAppTheme {
        ShotsScreenContent(
            uiState = ShotLogListUiState.Success(shots = emptyList(), canAddShot = false),
            onShotClick = {},
            onAddShot = {},
            onEditShot = {},
            onCopyCustomize = {},
            onNavigateToGear = {},
            onDeleteShot = {},
            onCopyShot = { _, _ -> },
            onFilterByCoffee = {},
            onClearFilter = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty — gear ready")
@Composable
private fun ShotsScreenEmptyPreview() {
    EspressoAppTheme {
        ShotsScreenContent(
            uiState = ShotLogListUiState.Success(shots = emptyList(), canAddShot = true),
            onShotClick = {},
            onAddShot = {},
            onEditShot = {},
            onCopyCustomize = {},
            onNavigateToGear = {},
            onDeleteShot = {},
            onCopyShot = { _, _ -> },
            onFilterByCoffee = {},
            onClearFilter = {},
        )
    }
}

@Preview(showBackground = true, name = "Filtered, no matches")
@Composable
private fun ShotsScreenFilteredEmptyPreview() {
    EspressoAppTheme {
        ShotsScreenContent(
            uiState = ShotLogListUiState.Success(
                shots = emptyList(),
                canAddShot = true,
                activeFilter = ShotFilter.CoffeeBean(
                    coffeeBeanId = "b1",
                    label = "Ethiopia Yirgacheffe",
                ),
                totalShotCount = 3,
            ),
            onShotClick = {},
            onAddShot = {},
            onEditShot = {},
            onCopyCustomize = {},
            onNavigateToGear = {},
            onDeleteShot = {},
            onCopyShot = { _, _ -> },
            onFilterByCoffee = {},
            onClearFilter = {},
        )
    }
}

@Preview(showBackground = true, name = "With shots")
@Composable
private fun ShotsScreenWithShotsPreview() {
    val now = System.currentTimeMillis()
    EspressoAppTheme {
        ShotsScreenContent(
            uiState = ShotLogListUiState.Success(
                shots = listOf(
                    ShotLog(
                        id = "1", coffeeBeanId = "b1", coffeeBeanName = "Ethiopia Yirgacheffe",
                        roasterName = "Nordic Coffee", grinderId = "g1", grinderName = "Niche Zero",
                        machineId = "m1", machineName = "ECM Synchronika", basketId = "bk1",
                        basketName = "IMS 18g", basketSizeClass = FilterBasketSize.SINGLE,
                        grindSetting = "2.5", doseGrams = 18.0,
                        yieldGrams = 36.0, ratio = 2.0, extractionTimeSeconds = 30,
                        brewTemperatureCelsius = 93.5, preInfusionSeconds = null, rating = 4,
                        notes = null, shotAt = now, createdAt = now, updatedAt = now,
                    ),
                    ShotLog(
                        id = "2", coffeeBeanId = "b1", coffeeBeanName = "Brazil Santos",
                        roasterName = null, grinderId = "g1", grinderName = "Niche Zero",
                        machineId = "m1", machineName = "ECM Synchronika", basketId = "bk1",
                        basketName = "IMS 18g", basketSizeClass = FilterBasketSize.DOUBLE,
                        grindSetting = null, doseGrams = 18.5,
                        yieldGrams = 40.0, ratio = 2.16, extractionTimeSeconds = null,
                        brewTemperatureCelsius = null, preInfusionSeconds = null, rating = null,
                        notes = null, shotAt = now - 86_400_000L, createdAt = now, updatedAt = now,
                    ),
                    ShotLog(
                        id = "3", coffeeBeanId = "b1", coffeeBeanName = "Kenya Nyeri",
                        roasterName = null, grinderId = "g1", grinderName = "Niche Zero",
                        machineId = "m1", machineName = "ECM Synchronika", basketId = "bk1",
                        basketName = "IMS 22g", basketSizeClass = FilterBasketSize.TRIPLE,
                        grindSetting = null, doseGrams = 22.0,
                        yieldGrams = 66.0, ratio = 3.0, extractionTimeSeconds = null,
                        brewTemperatureCelsius = null, preInfusionSeconds = null, rating = 2,
                        notes = null, shotAt = now - 172_800_000L, createdAt = now, updatedAt = now,
                    ),
                ),
                canAddShot = true,
            ),
            onShotClick = {},
            onAddShot = {},
            onEditShot = {},
            onCopyCustomize = {},
            onNavigateToGear = {},
            onDeleteShot = {},
            onCopyShot = { _, _ -> },
            onFilterByCoffee = {},
            onClearFilter = {},
        )
    }
}
