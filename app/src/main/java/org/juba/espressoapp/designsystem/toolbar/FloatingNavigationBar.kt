package org.juba.espressoapp.designsystem.toolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * A floating pill-shaped navigation bar. Each item carries its own [FloatingNavItem.onClick]
 * callback; the selected item is highlighted with a pill-shaped background.
 *
 * @param items Items to display as navigation destinations.
 * @param selectedIndex Index of the currently selected item.
 * @param modifier Modifier applied to the toolbar container.
 * @param shape Shape of the toolbar container.
 * @param containerColor Background color of the toolbar container.
 * @param shadowElevation Elevation of the drop shadow.
 */
@Composable
fun FloatingNavigationBar(
    items: List<FloatingNavItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingToolbarDefaults.shape,
    containerColor: Color = FloatingToolbarDefaults.containerColor(),
    shadowElevation: Dp = FloatingToolbarDefaults.shadowElevation,
) {
    FloatingToolbar(
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        shadowElevation = shadowElevation,
    ) {
        items.forEachIndexed { index, item ->
            FloatingNavItemView(
                item = item,
                selected = index == selectedIndex,
            )
        }
    }
}

@Composable
private fun FloatingNavItemView(
    item: FloatingNavItem,
    selected: Boolean,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        label = "navItemBackground",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "navItemContentColor",
    )

    Surface(
        onClick = item.onClick,
        shape = CircleShape,
        color = backgroundColor,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = stringResource(item.labelRes),
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(item.labelRes),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FloatingNavigationBarFirstSelectedPreview() {
    EspressoAppTheme {
        FloatingNavigationBar(
            items = listOf(
                FloatingNavItem(Icons.AutoMirrored.Filled.List, R.string.tab_shots),
                FloatingNavItem(Icons.Default.Coffee, R.string.tab_coffee),
                FloatingNavItem(Icons.Default.Build, R.string.tab_gear),
                FloatingNavItem(Icons.Default.Settings, R.string.tab_settings),
            ),
            selectedIndex = 0,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FloatingNavigationBarSecondSelectedPreview() {
    EspressoAppTheme {
        FloatingNavigationBar(
            items = listOf(
                FloatingNavItem(Icons.AutoMirrored.Filled.List, R.string.tab_shots),
                FloatingNavItem(Icons.Default.Coffee, R.string.tab_coffee),
                FloatingNavItem(Icons.Default.Build, R.string.tab_gear),
                FloatingNavItem(Icons.Default.Settings, R.string.tab_settings),
            ),
            selectedIndex = 1,
        )
    }
}
