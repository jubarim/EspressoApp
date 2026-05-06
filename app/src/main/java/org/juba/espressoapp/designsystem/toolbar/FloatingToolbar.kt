package org.juba.espressoapp.designsystem.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface

/**
 * Pill-shaped floating surface with a horizontal slot for arbitrary content.
 *
 * @param modifier Modifier applied to the outer [Surface].
 * @param shape Shape of the toolbar container.
 * @param containerColor Background color of the toolbar container.
 * @param shadowElevation Elevation of the drop shadow.
 * @param content Slot rendered inside a horizontal [Row].
 */
@Composable
fun FloatingToolbar(
    modifier: Modifier = Modifier,
    shape: Shape = FloatingToolbarDefaults.shape,
    containerColor: Color = FloatingToolbarDefaults.containerColor(),
    shadowElevation: Dp = FloatingToolbarDefaults.shadowElevation,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        shape = shape,
        color = containerColor,
        shadowElevation = shadowElevation,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
