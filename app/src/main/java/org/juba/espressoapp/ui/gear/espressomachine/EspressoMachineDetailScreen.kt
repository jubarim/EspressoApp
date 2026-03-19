package org.juba.espressoapp.ui.gear.espressomachine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.DetailActionsToolbar
import org.juba.espressoapp.designsystem.EmptyStateContent
import org.juba.espressoapp.domain.model.EspressoMachine
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspressoMachineDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EspressoMachineDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is EspressoMachineDetailUiState.Deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? EspressoMachineDetailUiState.Success)
                        ?.let { "${it.machine.brand} ${it.machine.model}" } ?: ""
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
                is EspressoMachineDetailUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                is EspressoMachineDetailUiState.Error -> EmptyStateContent(message = state.message)
                is EspressoMachineDetailUiState.Deleted -> Unit
                is EspressoMachineDetailUiState.Success -> EspressoMachineDetailContent(
                    machine = state.machine,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (uiState is EspressoMachineDetailUiState.Success) {
                DetailActionsToolbar(
                    onEdit = {
                        (uiState as? EspressoMachineDetailUiState.Success)?.machine?.id
                            ?.let { onEdit(it) }
                    },
                    onDelete = { showDeleteDialog = true },
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
            title = { Text(stringResource(R.string.espresso_machine_delete_dialog_title)) },
            text = { Text(stringResource(R.string.espresso_machine_delete_dialog_message)) },
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
}

@Composable
private fun EspressoMachineDetailContent(machine: EspressoMachine, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        machine.imageUri?.let { uri ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.espresso_machine_image_cd, machine.brand),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            machine.boilerType?.let { DetailRow(label = stringResource(R.string.field_boiler_type), value = it) }
            machine.pumpType?.let { DetailRow(label = stringResource(R.string.field_pump_type), value = it) }
            machine.groupHead?.let { DetailRow(label = stringResource(R.string.field_group_head), value = it) }
            DetailRow(
                label = stringResource(R.string.field_has_pressure_gauge),
                value = stringResource(if (machine.hasPressureGauge) R.string.yes else R.string.no),
            )
            machine.purchaseDate?.let {
                val display = remember(it) { formatDateWithAge(it) }
                DetailRow(label = stringResource(R.string.field_purchase_date), value = display)
            }
            machine.notes?.let { DetailRow(label = stringResource(R.string.field_notes), value = it) }
        }
    }
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

/**
 * Formats a Unix-ms timestamp as "MMM yyyy · X years ago" (or months for recent dates).
 * Dates within the current month are shown as "MMM yyyy · this month".
 */
private fun formatDateWithAge(timestampMs: Long): String {
    val date = SimpleDateFormat("MMM yyyy", Locale.US).format(Date(timestampMs))
    val diffMs = System.currentTimeMillis() - timestampMs
    val years = (diffMs / (365.25 * 24 * 3600 * 1000)).toLong()
    val months = (diffMs / (30.44 * 24 * 3600 * 1000)).toLong()
    val age = when {
        years >= 1 -> if (years == 1L) "1 year ago" else "$years years ago"
        months >= 1 -> if (months == 1L) "1 month ago" else "$months months ago"
        else -> "this month"
    }
    return "$date · $age"
}

@Preview(showBackground = true)
@Composable
private fun EspressoMachineDetailContentPreview() {
    EspressoAppTheme {
        EspressoMachineDetailContent(
            machine = EspressoMachine(
                id = "1",
                brand = "ECM",
                model = "Synchronika",
                boilerType = "HX",
                pumpType = "Rotary",
                groupHead = "E61",
                hasPressureGauge = true,
                purchaseDate = 1711929600000L,
                imageUri = null,
                notes = "Dual manometers.",
                createdAt = 0L,
                updatedAt = 0L,
            ),
        )
    }
}
