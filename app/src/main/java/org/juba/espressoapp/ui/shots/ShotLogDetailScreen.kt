package org.juba.espressoapp.ui.shots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.toolbar.DetailActionsToolbar
import org.juba.espressoapp.designsystem.EmptyStateContent
import org.juba.espressoapp.designsystem.QuickCopyDialog
import org.juba.espressoapp.domain.model.ShotLog
import org.juba.espressoapp.ui.main.LocalSnackbarHostState
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotLogDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onCopyCreated: (String) -> Unit,
    onCopyCustomize: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShotLogDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showCopyDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(R.string.copy_shot_success)

    LaunchedEffect(uiState) {
        if (uiState is ShotLogDetailUiState.Deleted) onBack()
    }

    LaunchedEffect(Unit) {
        viewModel.copyEvent.collect { newShotId ->
            scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
            onCopyCreated(newShotId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? ShotLogDetailUiState.Success)?.shot?.coffeeBeanName ?: ""
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (val state = uiState) {
                is ShotLogDetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ShotLogDetailUiState.Error -> EmptyStateContent(message = state.message)
                is ShotLogDetailUiState.Deleted -> Unit
                is ShotLogDetailUiState.Success -> ShotLogDetailContent(
                    shot = state.shot,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (uiState is ShotLogDetailUiState.Success) {
                DetailActionsToolbar(
                    onEdit = {
                        (uiState as? ShotLogDetailUiState.Success)?.shot?.id?.let { onEdit(it) }
                    },
                    onDelete = { showDeleteDialog = true },
                    onCopy = { showCopyDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.shot_log_delete_dialog_title)) },
            text = { Text(stringResource(R.string.shot_log_delete_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete()
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    if (showCopyDialog) {
        val shot = (uiState as? ShotLogDetailUiState.Success)?.shot
        if (shot != null) {
            QuickCopyDialog(
                title = stringResource(R.string.copy_shot_dialog_title),
                dateFieldLabel = stringResource(R.string.copy_shot_dialog_date_label),
                noDatePlaceholder = "",
                initialDate = null,
                defaultPickerDate = System.currentTimeMillis(),
                onDismiss = { showCopyDialog = false },
                onCustomize = {
                    showCopyDialog = false
                    onCopyCustomize(shot.id)
                },
                onCreate = { newDate ->
                    showCopyDialog = false
                    viewModel.copyShot(newDate ?: System.currentTimeMillis())
                },
            )
        }
    }
}

@Composable
private fun ShotLogDetailContent(shot: ShotLog, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        ShotMetricsCard(shot = shot, modifier = Modifier.padding(16.dp))

        HorizontalDivider()

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionLabel(stringResource(R.string.shot_label_shot_date))
            DetailRow(
                label = stringResource(R.string.shot_label_shot_date),
                value = formatShotDateTime(shot.shotAt),
            )

            SectionLabel(stringResource(R.string.shot_label_coffee_bean))
            shot.coffeeBeanName?.let { DetailRow(label = stringResource(R.string.shot_label_coffee_bean), value = it) }
            shot.roasterName?.let { DetailRow(label = stringResource(R.string.shot_label_roaster), value = it) }

            SectionLabel(stringResource(R.string.shot_label_grinder))
            shot.grinderName?.let { DetailRow(label = stringResource(R.string.shot_label_grinder), value = it) }
            shot.machineName?.let { DetailRow(label = stringResource(R.string.shot_label_machine), value = it) }
            shot.basketName?.let { DetailRow(label = stringResource(R.string.shot_label_basket), value = it) }

            shot.grindSetting?.let { DetailRow(label = stringResource(R.string.shot_label_grind_setting), value = it) }
            shot.extractionTimeSeconds?.let {
                DetailRow(label = stringResource(R.string.shot_label_extraction_time), value = "${it}s")
            }
            shot.brewTemperatureCelsius?.let {
                DetailRow(label = stringResource(R.string.shot_label_brew_temperature), value = "%.1f°C".format(it))
            }
            shot.preInfusionSeconds?.let {
                DetailRow(label = stringResource(R.string.shot_label_pre_infusion), value = "${it}s")
            }
            shot.rating?.let {
                DetailRow(label = stringResource(R.string.shot_label_rating), value = "$it / 5")
            }
            shot.notes?.let {
                DetailRow(label = stringResource(R.string.shot_label_notes), value = it)
            }
        }

        // Extra bottom padding so content isn't hidden behind the floating toolbar
        Box(modifier = Modifier.padding(bottom = 88.dp))
    }
}

@Composable
private fun ShotMetricsCard(shot: ShotLog, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "1:%.1f".format(shot.ratio),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1fg".format(shot.doseGrams),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = stringResource(R.string.shot_label_dose),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1fg".format(shot.yieldGrams),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = stringResource(R.string.shot_label_yield),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(top = 8.dp),
    )
}

@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun formatShotDateTime(timestampMs: Long): String =
    SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.US).format(Date(timestampMs))

@Preview(showBackground = true)
@Composable
private fun ShotLogDetailContentPreview() {
    EspressoAppTheme {
        ShotLogDetailContent(
            shot = ShotLog(
                id = "1",
                coffeeBeanId = "b1",
                coffeeBeanName = "Ethiopia Yirgacheffe",
                roasterName = "Nordic Coffee",
                grinderId = "g1",
                grinderName = "Niche Zero",
                machineId = "m1",
                machineName = "ECM Synchronika",
                basketId = "bk1",
                basketName = "IMS 18g",
                grindSetting = "2.5",
                doseGrams = 18.0,
                yieldGrams = 36.0,
                ratio = 2.0,
                extractionTimeSeconds = 30,
                brewTemperatureCelsius = 93.5,
                preInfusionSeconds = 5,
                rating = 4,
                notes = "Bright and fruity. Slightly under-extracted.",
                shotAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }
}
