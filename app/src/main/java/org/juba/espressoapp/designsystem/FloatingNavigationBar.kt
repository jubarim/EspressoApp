package org.juba.espressoapp.designsystem

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

data class FloatingNavItem(
    val icon: ImageVector,
    @param:StringRes val labelRes: Int,
)

/**
 * A floating pill-shaped navigation bar with always-visible icon + label for each item.
 * The selected item is highlighted with a pill-shaped background.
 */
@Composable
fun FloatingNavigationBar(
    items: List<FloatingNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                FloatingNavItemView(
                    item = item,
                    selected = index == selectedIndex,
                    onClick = { onItemSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun FloatingNavItemView(
    item: FloatingNavItem,
    selected: Boolean,
    onClick: () -> Unit,
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
        onClick = onClick,
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
            onItemSelected = {},
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
            onItemSelected = {},
        )
    }
}
