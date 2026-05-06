package org.juba.espressoapp.designsystem.toolbar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Default values shared by all [FloatingToolbar]-based components. */
internal object FloatingToolbarDefaults {
    val shape = RoundedCornerShape(32.dp)
    val shadowElevation = 6.dp

    @Composable
    fun containerColor(): Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
}
