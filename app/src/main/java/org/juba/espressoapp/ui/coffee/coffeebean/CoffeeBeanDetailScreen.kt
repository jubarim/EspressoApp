package org.juba.espressoapp.ui.coffee.coffeebean

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.juba.espressoapp.domain.model.CoffeeBean
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CoffeeBeanDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoffeeBeanDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is CoffeeBeanDetailUiState.Deleted) onBack()
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
                HorizontalFloatingToolbar(
                    expanded = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                ) {
                    IconButton(onClick = {
                        (uiState as? CoffeeBeanDetailUiState.Success)?.bean?.id
                            ?.let { onEdit(it) }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.cd_edit))
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cd_delete))
                    }
                }
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
