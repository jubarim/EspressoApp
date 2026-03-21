package org.juba.espressoapp.ui.coffee.coffeebean

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.launch
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.DetailActionsToolbar
import org.juba.espressoapp.designsystem.EmptyStateContent
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.ui.main.LocalSnackbarHostState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeBeanDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onCopyCustomize: (String) -> Unit,
    onCopyCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoffeeBeanDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showCopyDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(R.string.copy_bean_success)

    LaunchedEffect(uiState) {
        if (uiState is CoffeeBeanDetailUiState.Deleted) onBack()
    }

    LaunchedEffect(Unit) {
        viewModel.copyEvent.collect { newBeanId ->
            scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
            onCopyCreated(newBeanId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val name = (uiState as? CoffeeBeanDetailUiState.Success)?.bean?.name ?: ""
                    Text(name)
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
                is CoffeeBeanDetailUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                is CoffeeBeanDetailUiState.Error -> EmptyStateContent(message = state.message)
                is CoffeeBeanDetailUiState.Deleted -> Unit
                is CoffeeBeanDetailUiState.Success -> CoffeeBeanDetailContent(
                    bean = state.bean,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (uiState is CoffeeBeanDetailUiState.Success) {
                DetailActionsToolbar(
                    onEdit = {
                        (uiState as? CoffeeBeanDetailUiState.Success)?.bean?.id
                            ?.let { onEdit(it) }
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
            title = { Text(stringResource(R.string.coffee_bean_delete_dialog_title)) },
            text = { Text(stringResource(R.string.coffee_bean_delete_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete()
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }

    if (showCopyDialog) {
        val bean = (uiState as? CoffeeBeanDetailUiState.Success)?.bean
        if (bean != null) {
            CopyDialog(
                initialRoastDate = bean.roastDate,
                onDismiss = { showCopyDialog = false },
                onCustomize = {
                    showCopyDialog = false
                    onCopyCustomize(bean.id)
                },
                onCreate = { newDate ->
                    showCopyDialog = false
                    viewModel.copyBean(newDate)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CopyDialog(
    initialRoastDate: Long?,
    onDismiss: () -> Unit,
    onCustomize: () -> Unit,
    onCreate: (Long?) -> Unit,
) {
    var selectedDate by remember { mutableStateOf(initialRoastDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
    val dateLabel = selectedDate?.let { dateFormatter.format(Date(it)) }
        ?: stringResource(R.string.copy_bean_dialog_no_date)

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.copy_bean_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.copy_bean_dialog_roast_date_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedCard(onClick = { showDatePicker = true }) {
                        Text(
                            text = dateLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    TextButton(onClick = onCustomize) {
                        Text(stringResource(R.string.action_customize))
                    }
                    TextButton(
                        onClick = { onCreate(selectedDate) },
                        enabled = selectedDate != initialRoastDate,
                    ) {
                        Text(stringResource(R.string.action_create))
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text(stringResource(R.string.picker_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun CoffeeBeanDetailContent(bean: CoffeeBean, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        bean.imageUri?.let { uri ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.coffee_bean_image_cd, bean.name),
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
            bean.roasterName?.let { DetailRow(label = stringResource(R.string.field_roaster), value = it) }
            bean.origin?.let { DetailRow(label = stringResource(R.string.field_origin), value = it) }
            bean.process?.let { DetailRow(label = stringResource(R.string.field_process), value = it) }
            bean.roastLevel?.let { DetailRow(label = stringResource(R.string.field_roast_level), value = it) }
            bean.roastDate?.let {
                val formatted = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(it))
                DetailRow(label = stringResource(R.string.field_roast_date), value = formatted)
            }
            bean.notes?.let { DetailRow(label = stringResource(R.string.field_notes), value = it) }
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
