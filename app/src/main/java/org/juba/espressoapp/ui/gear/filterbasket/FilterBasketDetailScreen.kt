package org.juba.espressoapp.ui.gear.filterbasket

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
import org.juba.espressoapp.domain.model.FilterBasket
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBasketDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FilterBasketDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is FilterBasketDetailUiState.Deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? FilterBasketDetailUiState.Success)?.let {
                        if (it.basket.model != null) "${it.basket.brand} ${it.basket.model}"
                        else it.basket.brand
                    } ?: ""
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
                is FilterBasketDetailUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                is FilterBasketDetailUiState.Error -> EmptyStateContent(message = state.message)
                is FilterBasketDetailUiState.Deleted -> Unit
                is FilterBasketDetailUiState.Success -> FilterBasketDetailContent(
                    basket = state.basket,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            if (uiState is FilterBasketDetailUiState.Success) {
                DetailActionsToolbar(
                    onEdit = {
                        (uiState as? FilterBasketDetailUiState.Success)?.basket?.id
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
            title = { Text(stringResource(R.string.filter_basket_delete_dialog_title)) },
            text = { Text(stringResource(R.string.filter_basket_delete_dialog_message)) },
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
private fun FilterBasketDetailContent(basket: FilterBasket, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        basket.imageUri?.let { uri ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.filter_basket_image_cd, basket.brand),
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
            basket.type?.let { DetailRow(label = stringResource(R.string.field_basket_type), value = it) }
            basket.sizeGrams?.let { DetailRow(label = stringResource(R.string.field_size), value = it) }
            basket.diameter?.let { DetailRow(label = stringResource(R.string.field_diameter), value = it) }
            basket.purchaseDate?.let {
                val display = remember(it) { formatDateWithAge(it) }
                DetailRow(label = stringResource(R.string.field_purchase_date), value = display)
            }
            basket.notes?.let { DetailRow(label = stringResource(R.string.field_notes), value = it) }
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
private fun FilterBasketDetailContentPreview() {
    EspressoAppTheme {
        FilterBasketDetailContent(
            basket = FilterBasket(
                id = "1",
                brand = "IMS",
                model = "Competition",
                sizeGrams = "18g",
                type = "Precision",
                diameter = "58mm",
                purchaseDate = 1711929600000L,
                imageUri = null,
                notes = "Precision laser-cut holes.",
                createdAt = 0L,
                updatedAt = 0L,
            ),
        )
    }
}
