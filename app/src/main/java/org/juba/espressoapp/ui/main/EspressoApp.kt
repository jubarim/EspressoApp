package org.juba.espressoapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.juba.espressoapp.ui.gear.espressomachine.EspressoMachineDetailScreen
import org.juba.espressoapp.ui.gear.espressomachine.EspressoMachineFormScreen
import org.juba.espressoapp.ui.gear.espressomachine.EspressoMachineListScreen
import org.juba.espressoapp.ui.gear.filterbasket.FilterBasketDetailScreen
import org.juba.espressoapp.ui.gear.filterbasket.FilterBasketFormScreen
import org.juba.espressoapp.ui.gear.filterbasket.FilterBasketListScreen
import org.juba.espressoapp.ui.gear.grinder.GrinderDetailScreen
import org.juba.espressoapp.ui.gear.grinder.GrinderFormScreen
import org.juba.espressoapp.ui.gear.grinder.GrinderListScreen
import org.juba.espressoapp.ui.settings.SettingsScreen
import org.juba.espressoapp.ui.shots.ShotLogDetailScreen
import org.juba.espressoapp.ui.shots.ShotLogFormScreen
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
        AppRoutes.SHOTS,
        AppRoutes.SHOT_DETAIL, AppRoutes.SHOT_FORM -> AppDestinations.SHOTS
        AppRoutes.COFFEE_HOME, AppRoutes.ROASTER_LIST,
        AppRoutes.ROASTER_DETAIL, AppRoutes.ROASTER_FORM,
        AppRoutes.COFFEE_BEAN_LIST, AppRoutes.COFFEE_BEAN_DETAIL,
        AppRoutes.COFFEE_BEAN_FORM -> AppDestinations.COFFEE
        AppRoutes.GEAR, AppRoutes.GRINDER_LIST,
        AppRoutes.GRINDER_DETAIL, AppRoutes.GRINDER_FORM,
        AppRoutes.ESPRESSO_MACHINE_LIST, AppRoutes.ESPRESSO_MACHINE_DETAIL,
        AppRoutes.ESPRESSO_MACHINE_FORM,
        AppRoutes.FILTER_BASKET_LIST, AppRoutes.FILTER_BASKET_DETAIL,
        AppRoutes.FILTER_BASKET_FORM -> AppDestinations.GEAR
        AppRoutes.SETTINGS -> AppDestinations.SETTINGS
        else -> AppDestinations.SHOTS
    }

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
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
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(navController = navController, startDestination = AppRoutes.SHOTS) {
                    composable(AppRoutes.SHOTS) {
                        ShotsScreen(
                            onShotClick = { id -> navController.navigate("shot_detail/$id") },
                            onAddShot = { navController.navigate("shot_form") },
                            onNavigateToGear = {
                                navController.navigate(AppDestinations.GEAR.startRoute) {
                                    popUpTo(AppRoutes.SHOTS) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                    composable(AppRoutes.SHOT_DETAIL) {
                        ShotLogDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = { id -> navController.navigate("shot_form?shotId=$id") },
                        )
                    }
                    composable(
                        AppRoutes.SHOT_FORM,
                        arguments = listOf(navArgument("shotId") { nullable = true; defaultValue = null }),
                    ) {
                        ShotLogFormScreen(onDismiss = { navController.popBackStack() })
                    }
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
                            onCopyCustomize = { id -> navController.navigate("coffee_bean_form?sourceBeanId=$id") },
                            onCopyCreated = { id -> navController.navigate("coffee_bean_detail/$id") },
                        )
                    }
                    composable(
                        AppRoutes.COFFEE_BEAN_FORM,
                        arguments = listOf(
                            navArgument("beanId") { nullable = true; defaultValue = null },
                            navArgument("sourceBeanId") { nullable = true; defaultValue = null },
                        ),
                    ) {
                        CoffeeBeanFormScreen(onDismiss = { navController.popBackStack() })
                    }
                    composable(AppRoutes.GEAR) {
                        GearScreen(onCategoryClick = { route -> navController.navigate(route) })
                    }
                    composable(AppRoutes.GRINDER_LIST) {
                        GrinderListScreen(
                            onBack = { navController.popBackStack() },
                            onGrinderClick = { id -> navController.navigate("grinder_detail/$id") },
                            onAddGrinder = { navController.navigate("grinder_form") },
                        )
                    }
                    composable(AppRoutes.GRINDER_DETAIL) {
                        GrinderDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = { id -> navController.navigate("grinder_form?grinderId=$id") },
                        )
                    }
                    composable(
                        AppRoutes.GRINDER_FORM,
                        arguments = listOf(navArgument("grinderId") { nullable = true; defaultValue = null }),
                    ) {
                        GrinderFormScreen(onDismiss = { navController.popBackStack() })
                    }
                    composable(AppRoutes.ESPRESSO_MACHINE_LIST) {
                        EspressoMachineListScreen(
                            onBack = { navController.popBackStack() },
                            onMachineClick = { id -> navController.navigate("espresso_machine_detail/$id") },
                            onAddMachine = { navController.navigate("espresso_machine_form") },
                        )
                    }
                    composable(AppRoutes.ESPRESSO_MACHINE_DETAIL) {
                        EspressoMachineDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = { id -> navController.navigate("espresso_machine_form?machineId=$id") },
                        )
                    }
                    composable(
                        AppRoutes.ESPRESSO_MACHINE_FORM,
                        arguments = listOf(navArgument("machineId") { nullable = true; defaultValue = null }),
                    ) {
                        EspressoMachineFormScreen(onDismiss = { navController.popBackStack() })
                    }
                    composable(AppRoutes.FILTER_BASKET_LIST) {
                        FilterBasketListScreen(
                            onBack = { navController.popBackStack() },
                            onBasketClick = { id -> navController.navigate("filter_basket_detail/$id") },
                            onAddBasket = { navController.navigate("filter_basket_form") },
                        )
                    }
                    composable(AppRoutes.FILTER_BASKET_DETAIL) {
                        FilterBasketDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = { id -> navController.navigate("filter_basket_form?basketId=$id") },
                        )
                    }
                    composable(
                        AppRoutes.FILTER_BASKET_FORM,
                        arguments = listOf(navArgument("basketId") { nullable = true; defaultValue = null }),
                    ) {
                        FilterBasketFormScreen(onDismiss = { navController.popBackStack() })
                    }
                    composable(AppRoutes.SETTINGS) { SettingsScreen() }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}
