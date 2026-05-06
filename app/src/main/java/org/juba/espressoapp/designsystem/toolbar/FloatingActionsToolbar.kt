package org.juba.espressoapp.designsystem.toolbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * Generic floating toolbar that renders a list of icon-only action items.
 *
 * @param items Items to display; each item's [FloatingNavItem.onClick] is invoked on tap.
 * @param modifier Modifier applied to the toolbar container.
 * @param shape Shape of the toolbar container.
 * @param containerColor Background color of the toolbar container.
 * @param shadowElevation Elevation of the drop shadow.
 */
@Composable
fun FloatingActionsToolbar(
    items: List<FloatingNavItem>,
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
        items.forEach { item ->
            IconButton(onClick = item.onClick) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = stringResource(item.labelRes),
                )
            }
        }
    }
}

/**
 * Convenience wrapper over [FloatingActionsToolbar] for detail screens.
 * Positioning is left to the caller via [modifier].
 */
@Composable
fun DetailActionsToolbar(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onCopy: (() -> Unit)? = null,
    shape: Shape = FloatingToolbarDefaults.shape,
    containerColor: Color = FloatingToolbarDefaults.containerColor(),
    shadowElevation: Dp = FloatingToolbarDefaults.shadowElevation,
) {
    val items = buildList {
        add(FloatingNavItem(Icons.Default.Edit, R.string.cd_edit, onClick = onEdit))
        add(FloatingNavItem(Icons.Default.Delete, R.string.cd_delete, onClick = onDelete))
        if (onCopy != null) add(FloatingNavItem(Icons.Default.ContentCopy, R.string.cd_copy, onClick = onCopy))
    }
    FloatingActionsToolbar(items, modifier, shape, containerColor, shadowElevation)
}

@Preview(showBackground = true)
@Composable
private fun DetailActionsToolbarPreview() {
    EspressoAppTheme {
        DetailActionsToolbar(
            onEdit = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailActionsToolbarWithCopyPreview() {
    EspressoAppTheme {
        DetailActionsToolbar(
            onEdit = {},
            onDelete = {},
            onCopy = {},
        )
    }
}
