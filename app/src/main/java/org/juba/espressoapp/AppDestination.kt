package org.juba.espressoapp

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    SHOTS(R.string.tab_shots, Icons.AutoMirrored.Filled.List),
    COFFEE(R.string.tab_coffee, Icons.Default.Favorite),
    GEAR(R.string.tab_gear, Icons.Default.Build),
    SETTINGS(R.string.tab_settings, Icons.Default.Settings),
}
