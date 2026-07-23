package org.juba.espressoapp.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.juba.espressoapp.designsystem.toolbar.FloatingNavItem
import org.juba.espressoapp.designsystem.toolbar.FloatingNavigationBar
import org.juba.espressoapp.ui.auth.AuthScreen
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

    // Navigate to auth when the Firebase session is cleared at runtime (e.g. after sign-out).
    LaunchedEffect(Unit) {
        callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
            FirebaseAuth.getInstance().addAuthStateListener(listener)
            awaitClose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
        }.collect { user ->
            if (user == null && currentRoute != AppRoutes.AUTH) {
                navController.navigate(AppRoutes.AUTH) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

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
    val selectedIndex = AppDestinations.entries.indexOf(selectedDestination)
    val navItems: List<FloatingNavItem> = AppDestinations.entries.map { dest ->
        FloatingNavItem(dest.icon, dest.labelRes, onClick = {
            navController.navigate(dest.startRoute) {
                popUpTo(AppRoutes.SHOTS) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        })
    }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                AnimatedVisibility(
                    visible = showNavBar,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut() + shrinkVertically(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        FloatingNavigationBar(
                            items = navItems,
                            selectedIndex = selectedIndex,
                        )
                    }
                }
            },
        ) { innerPadding ->
            CompositionLocalProvider(LocalNavBarPadding provides innerPadding.calculateBottomPadding()) {

                Box(modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxSize()) {
                    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
                            AppRoutes.SHOTS
                        } else {
                            AppRoutes.AUTH
                        }
                NavHost(navController = navController, startDestination = startDestination) {
                        composable(AppRoutes.AUTH) {
                            AuthScreen(
                                onAuthSuccess = {
                                    navController.navigate(AppRoutes.SHOTS) {
                                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                                    }
                                },
                            )
                        }
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
                                onCopyCreated = { id -> navController.navigate("shot_detail/$id") },
                                onCopyCustomize = { id -> navController.navigate("shot_form?sourceShotId=$id") },
                            )
                        }
                        composable(
                            AppRoutes.SHOT_FORM,
                            arguments = listOf(
                                navArgument("shotId") { nullable = true; defaultValue = null },
                                navArgument("sourceShotId") { nullable = true; defaultValue = null },
                            ),
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
                }
            }
        }
    }
}
