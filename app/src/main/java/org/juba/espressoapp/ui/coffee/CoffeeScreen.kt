package org.juba.espressoapp.ui.coffee

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.juba.espressoapp.R

private enum class CoffeeCategory(@param:StringRes val labelRes: Int, val route: String) {
    ROASTERS(R.string.coffee_category_roasters, "roaster_list"),
    COFFEE_BEANS(R.string.coffee_category_coffee_beans, "coffee_bean_list"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeHomeScreen(
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_coffee)) }) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            items(CoffeeCategory.entries) { category ->
                ListItem(
                    headlineContent = { Text(stringResource(category.labelRes)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                            )
                        }
                    },
                    modifier = Modifier.clickable { onCategoryClick(category.route) },
                )
                HorizontalDivider()
            }
        }
    }
}
