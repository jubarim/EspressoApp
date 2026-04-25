package org.juba.espressoapp.ui.gear.filterbasket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import org.juba.espressoapp.designsystem.EmptyStateContent
import org.juba.espressoapp.domain.model.FilterBasket
import org.juba.espressoapp.ui.theme.EspressoAppTheme

@Composable
fun FilterBasketListScreen(
    onBack: () -> Unit,
    onBasketClick: (String) -> Unit,
    onAddBasket: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FilterBasketListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FilterBasketListContent(
        uiState = uiState,
        onBack = onBack,
        onBasketClick = onBasketClick,
        onAddBasket = onAddBasket,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBasketListContent(
    uiState: FilterBasketListUiState,
    onBack: () -> Unit,
    onBasketClick: (String) -> Unit,
    onAddBasket: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.gear_category_filter_baskets)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBasket) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_filter_basket))
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        when (val state = uiState) {
            is FilterBasketListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is FilterBasketListUiState.Error -> EmptyStateContent(
                message = state.message,
                modifier = Modifier.padding(innerPadding),
            )

            is FilterBasketListUiState.Success -> if (state.baskets.isEmpty()) {
                EmptyStateContent(
                    message = stringResource(R.string.filter_basket_list_empty_message),
                    actionLabel = stringResource(R.string.filter_basket_add),
                    onAction = onAddBasket,
                    modifier = Modifier.padding(innerPadding),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding,
                ) {
                    items(state.baskets, key = { it.id }) { basket ->
                        ListItem(
                            headlineContent = {
                                val label = if (basket.model != null) "${basket.brand} ${basket.model}" else basket.brand
                                Text(label)
                            },
                            supportingContent = basket.type?.let { type -> { Text(type) } },
                            leadingContent = { FilterBasketAvatar(basket) },
                            modifier = Modifier.clickable { onBasketClick(basket.id) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterBasketAvatar(basket: FilterBasket) {
    val imageUri = basket.imageUri
    if (imageUri != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.filter_basket_image_cd, basket.brand),
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
                text = basket.brand.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterBasketListScreenEmptyPreview() {
    EspressoAppTheme {
        FilterBasketListContent(
            uiState = FilterBasketListUiState.Success(emptyList()),
            onBack = {},
            onBasketClick = {},
            onAddBasket = {},
        )
    }
}
