package org.juba.espressoapp.ui.gear

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.juba.espressoapp.R
import org.juba.espressoapp.designsystem.CategoryListItem
import org.juba.espressoapp.ui.theme.EspressoAppTheme

private enum class GearCategory(
    @param:StringRes val labelRes: Int,
    val route: String?,
) {
    GRINDERS(R.string.gear_category_grinders, "grinder_list"),
    ESPRESSO_MACHINES(R.string.gear_category_espresso_machines, "espresso_machine_list"),
    FILTER_BASKETS(R.string.gear_category_filter_baskets, null),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {},
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_gear)) }) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            items(GearCategory.entries) { category ->
                CategoryListItem(
                    label = stringResource(category.labelRes),
                    onClick = { category.route?.let { onCategoryClick(it) } },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GearScreenPreview() {
    EspressoAppTheme {
        GearScreen()
    }
}
