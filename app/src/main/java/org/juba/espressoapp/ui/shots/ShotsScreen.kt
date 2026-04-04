package org.juba.espressoapp.ui.shots

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.EmptyStateContent
import org.juba.espressoapp.domain.model.ShotLog
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShotsScreen(
    onShotClick: (String) -> Unit,
    onAddShot: () -> Unit,
    onNavigateToGear: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShotLogListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShotsScreenContent(
        uiState = uiState,
        onShotClick = onShotClick,
        onAddShot = onAddShot,
        onNavigateToGear = onNavigateToGear,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShotsScreenContent(
    uiState: ShotLogListUiState,
    onShotClick: (String) -> Unit,
    onAddShot: () -> Unit,
    onNavigateToGear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_shots)) }) },
        floatingActionButton = {
            if (uiState is ShotLogListUiState.Success && uiState.canAddShot) {
                FloatingActionButton(onClick = onAddShot) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_shot))
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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

                uiState.shots.isEmpty() -> EmptyStateContent(
                    message = stringResource(R.string.shot_log_list_empty_no_shots),
                    actionLabel = stringResource(R.string.shot_log_add),
                    onAction = onAddShot,
                    modifier = Modifier.padding(innerPadding),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding,
                ) {
                    items(uiState.shots, key = { it.id }) { shot ->
                        ShotListItem(
                            shot = shot,
                            onClick = { onShotClick(shot.id) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ShotListItem(shot: ShotLog, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            val headline = shot.coffeeBeanName ?: "Unknown Bean"
            Text(headline)
        },
        supportingContent = {
            val parts = buildList {
                add("%.1fg → %.1fg".format(shot.doseGrams, shot.yieldGrams))
                shot.extractionTimeSeconds?.let { add("${it}s") }
            }
            Text(parts.joinToString(" · "))
        },
        leadingContent = { RatioBadge(ratio = shot.ratio) },
        trailingContent = {
            Text(
                text = formatShotDate(shot.shotAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        modifier = modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun RatioBadge(ratio: Double, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "1:%.1f".format(ratio),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
            onNavigateToGear = {},
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
            onNavigateToGear = {},
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
                        basketName = "IMS 18g", grindSetting = "2.5", doseGrams = 18.0,
                        yieldGrams = 36.0, ratio = 2.0, extractionTimeSeconds = 30,
                        brewTemperatureCelsius = 93.5, preInfusionSeconds = null, rating = 4,
                        notes = null, shotAt = now, createdAt = now, updatedAt = now,
                    ),
                    ShotLog(
                        id = "2", coffeeBeanId = "b1", coffeeBeanName = "Brazil Santos",
                        roasterName = null, grinderId = "g1", grinderName = "Niche Zero",
                        machineId = "m1", machineName = "ECM Synchronika", basketId = "bk1",
                        basketName = "IMS 18g", grindSetting = null, doseGrams = 18.5,
                        yieldGrams = 40.0, ratio = 2.16, extractionTimeSeconds = null,
                        brewTemperatureCelsius = null, preInfusionSeconds = null, rating = null,
                        notes = null, shotAt = now - 86_400_000L, createdAt = now, updatedAt = now,
                    ),
                ),
                canAddShot = true,
            ),
            onShotClick = {},
            onAddShot = {},
            onNavigateToGear = {},
        )
    }
}
