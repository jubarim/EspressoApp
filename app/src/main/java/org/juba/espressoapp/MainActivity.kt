package org.juba.espressoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import org.juba.espressoapp.ui.coffee.CoffeeScreen
import org.juba.espressoapp.ui.gear.GearScreen
import org.juba.espressoapp.ui.settings.SettingsScreen
import org.juba.espressoapp.ui.shots.ShotsScreen
import org.juba.espressoapp.ui.theme.EspressoAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EspressoAppTheme {
                EspressoApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun EspressoApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.SHOTS) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestination.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = stringResource(it.labelRes)) },
                    label = { Text(stringResource(it.labelRes)) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it },
                )
            }
        },
    ) {
        when (currentDestination) {
            AppDestination.SHOTS -> ShotsScreen()
            AppDestination.COFFEE -> CoffeeScreen()
            AppDestination.GEAR -> GearScreen()
            AppDestination.SETTINGS -> SettingsScreen()
        }
    }
}
