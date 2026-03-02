package org.juba.espressoapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val label: String,
    val icon: ImageVector,
) {
    SHOTS("Shots", Icons.AutoMirrored.Filled.List),
    COFFEE("Coffee", Icons.Default.Favorite),
    GEAR("Gear", Icons.Default.Build),
    SETTINGS("Settings", Icons.Default.Settings),
}
