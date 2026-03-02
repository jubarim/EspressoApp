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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.juba.espressoapp.ui.coffee.roaster.RoasterDetailScreen
import org.juba.espressoapp.ui.coffee.roaster.RoasterFormScreen
import org.juba.espressoapp.ui.coffee.roaster.RoasterListScreen

private enum class CoffeeCategory(val label: String, val route: String) {
    ROASTERS("Roasters", "roaster_list"),
    COFFEE_BEANS("Coffee Beans", "coffee_bean_list"),
}

private const val ROUTE_HOME = "coffee_home"
private const val ROUTE_ROASTER_LIST = "roaster_list"
private const val ROUTE_ROASTER_DETAIL = "roaster_detail/{roasterId}"
private const val ROUTE_ROASTER_FORM = "roaster_form?roasterId={roasterId}"

@Composable
fun CoffeeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_HOME,
        modifier = modifier,
    ) {
        composable(ROUTE_HOME) {
            CoffeeHomeScreen(
                onCategoryClick = { route -> navController.navigate(route) },
            )
        }
        composable(ROUTE_ROASTER_LIST) {
            RoasterListScreen(
                onBack = { navController.popBackStack() },
                onRoasterClick = { id -> navController.navigate("roaster_detail/$id") },
                onAddRoaster = { navController.navigate("roaster_form") },
            )
        }
        composable(ROUTE_ROASTER_DETAIL) {
            RoasterDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("roaster_form?roasterId=$id") },
            )
        }
        composable(ROUTE_ROASTER_FORM) {
            RoasterFormScreen(
                onDismiss = { navController.popBackStack() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoffeeHomeScreen(
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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
