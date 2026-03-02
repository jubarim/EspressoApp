package org.juba.espressoapp.ui.coffee

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
import androidx.compose.ui.tooling.preview.Preview
import org.juba.espressoapp.ui.theme.EspressoAppTheme

private enum class CoffeeCategory(val label: String) {
    ROASTERS("Roasters"),
    COFFEE_BEANS("Coffee Beans"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Coffee") }) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            items(CoffeeCategory.entries) { category ->
                ListItem(
                    headlineContent = { Text(category.label) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("0")
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                            )
                        }
                    },
                    modifier = Modifier.clickable { },
                )
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoffeeScreenPreview() {
    EspressoAppTheme {
        CoffeeScreen()
    }
}
