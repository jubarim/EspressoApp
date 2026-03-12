package org.juba.espressoapp.ui.main

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.juba.espressoapp.ui.coffee.CoffeeHomeScreen
import org.juba.espressoapp.ui.coffee.coffeebean.CoffeeBeanDetailScreen
import org.juba.espressoapp.ui.coffee.coffeebean.CoffeeBeanFormScreen
import org.juba.espressoapp.ui.coffee.coffeebean.CoffeeBeanListScreen
import org.juba.espressoapp.ui.coffee.roaster.RoasterDetailScreen
import org.juba.espressoapp.ui.coffee.roaster.RoasterFormScreen
import org.juba.espressoapp.ui.coffee.roaster.RoasterListScreen
import org.juba.espressoapp.ui.gear.GearScreen
import org.juba.espressoapp.ui.settings.SettingsScreen
import org.juba.espressoapp.ui.shots.ShotsScreen

@PreviewScreenSizes
@Composable
fun EspressoApp() {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    // Only show bottom navigation if current route is in showBottomNavBar set
    val showNavBar = currentRoute in showBottomNavBar

    val selectedDestination = when (currentRoute) {
        AppRoutes.SHOTS -> AppDestinations.SHOTS
        AppRoutes.COFFEE_HOME, AppRoutes.ROASTER_LIST,
        AppRoutes.ROASTER_DETAIL, AppRoutes.ROASTER_FORM,
        AppRoutes.COFFEE_BEAN_LIST, AppRoutes.COFFEE_BEAN_DETAIL,
        AppRoutes.COFFEE_BEAN_FORM -> AppDestinations.COFFEE
        AppRoutes.GEAR -> AppDestinations.GEAR
        AppRoutes.SETTINGS -> AppDestinations.SETTINGS
        else -> AppDestinations.SHOTS
    }

    NavigationSuiteScaffold(
        layoutType = if (showNavBar) {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        } else {
            NavigationSuiteType.None
        },
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = stringResource(destination.labelRes)) },
                    label = { Text(stringResource(destination.labelRes)) },
                    selected = destination == selectedDestination,
                    onClick = {
                        navController.navigate(destination.startRoute) {
                            popUpTo(AppRoutes.SHOTS) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) {
        NavHost(navController = navController, startDestination = AppRoutes.SHOTS) {
            composable(AppRoutes.SHOTS) { ShotsScreen() }
            composable(AppRoutes.COFFEE_HOME) {
                CoffeeHomeScreen(onCategoryClick = { route -> navController.navigate(route) })
            }
            composable(AppRoutes.ROASTER_LIST) {
                RoasterListScreen(
                    onBack = { navController.popBackStack() },
                    onRoasterClick = { id -> navController.navigate("roaster_detail/$id") },
                    onAddRoaster = { navController.navigate("roaster_form") },
                )
            }
            composable(AppRoutes.ROASTER_DETAIL) {
                RoasterDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate("roaster_form?roasterId=$id") },
                )
            }
            composable(
                AppRoutes.ROASTER_FORM,
                arguments = listOf(navArgument("roasterId") { nullable = true; defaultValue = null }),
            ) {
                RoasterFormScreen(onDismiss = { navController.popBackStack() })
            }
            composable(AppRoutes.COFFEE_BEAN_LIST) {
                CoffeeBeanListScreen(
                    onBack = { navController.popBackStack() },
                    onBeanClick = { id -> navController.navigate("coffee_bean_detail/$id") },
                    onAddBean = { navController.navigate("coffee_bean_form") },
                )
            }
            composable(AppRoutes.COFFEE_BEAN_DETAIL) {
                CoffeeBeanDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate("coffee_bean_form?beanId=$id") },
                )
            }
            composable(
                AppRoutes.COFFEE_BEAN_FORM,
                arguments = listOf(navArgument("beanId") { nullable = true; defaultValue = null }),
            ) {
                CoffeeBeanFormScreen(onDismiss = { navController.popBackStack() })
            }
            composable(AppRoutes.GEAR) { GearScreen() }
            composable(AppRoutes.SETTINGS) { SettingsScreen() }
        }
    }
}
