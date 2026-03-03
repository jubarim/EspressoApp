package org.juba.espressoapp.ui.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import org.juba.espressoapp.R

enum class AppDestinations(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val startRoute: String,
) {
    SHOTS(R.string.tab_shots, Icons.AutoMirrored.Filled.List, "shots"),
    COFFEE(R.string.tab_coffee, Icons.Default.Favorite, "coffee_home"),
    GEAR(R.string.tab_gear, Icons.Default.Build, "gear"),
    SETTINGS(R.string.tab_settings, Icons.Default.Settings, "settings"),
}
