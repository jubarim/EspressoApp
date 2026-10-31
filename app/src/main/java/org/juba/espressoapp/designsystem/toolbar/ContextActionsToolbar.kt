package org.juba.espressoapp.designsystem.toolbar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.FloatingToolbarDefaults as Material3FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/** Size of the contextual FAB. Change it here to resize the button and its shape transition. */
private val ContextFabSize = 56.dp

/** Corner radius of the collapsed FAB as a fraction of [ContextFabSize]. */
private const val CollapsedCornerRatio = 0.3f

/**
 * Floating toolbar for contextual actions on a long-pressed list item.
 *
 * The toolbar is anchored to a vibrant floating action button. When [expanded] is false, only the
 * button is visible and it shows [collapsedFabIcon]. When [expanded] is true, the actions expand
 * from the button and its icon crossfades to Close. The whole component stays composed, so the
 * Material 3 expressive expansion animation runs on every selection change.
 *
 * The button is 56dp and morphs from a rounded square while collapsed to a circle while expanded.
 * Its size is [ContextFabSize], shared with the shape transition. The toolbar itself controls the
 * FAB slot size, so the button is end-aligned to keep its position fixed.
 *
 * @param expanded Whether the contextual actions are shown.
 * @param items Actions to display while expanded; each item's [FloatingNavItem.onClick] is invoked
 *   on tap.
 * @param onFabClick Invoked when the floating action button is tapped. Use it to run the primary
 *   action while collapsed and to clear the selection while expanded.
 * @param collapsedFabIcon Icon shown on the button while collapsed.
 * @param collapsedFabContentDescription Accessibility description of [collapsedFabIcon].
 * @param modifier Modifier applied to the toolbar container.
 */
@Composable
fun ContextActionsToolbar(
    expanded: Boolean,
    items: List<FloatingNavItem>,
    onFabClick: () -> Unit,
    collapsedFabIcon: ImageVector,
    collapsedFabContentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = Material3FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    val closeContentDescription = stringResource(R.string.cd_close)
    val fabCornerRadius by animateDpAsState(
        targetValue = if (expanded) ContextFabSize / 2 else ContextFabSize * CollapsedCornerRatio,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "contextFabShape",
    )

    HorizontalFloatingToolbar(
        expanded = expanded,
        modifier = modifier,
        colors = colors,
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Surface(
                    onClick = onFabClick,
                    modifier = Modifier.size(ContextFabSize),
                    shape = RoundedCornerShape(fabCornerRadius),
                    color = colors.fabContainerColor,
                    contentColor = colors.fabContentColor,
                    shadowElevation = 6.dp,
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Crossfade(
                            targetState = expanded,
                            label = "contextFabIcon",
                        ) { isExpanded ->
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.Close else collapsedFabIcon,
                                contentDescription = if (isExpanded) {
                                    closeContentDescription
                                } else {
                                    collapsedFabContentDescription
                                },
                            )
                        }
                    }
                }
            }
        },
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

@Preview(showBackground = true, name = "Collapsed")
@Composable
private fun ContextActionsToolbarCollapsedPreview() {
    EspressoAppTheme {
        ContextActionsToolbar(
            expanded = false,
            items = emptyList(),
            onFabClick = {},
            collapsedFabIcon = Icons.Default.Add,
            collapsedFabContentDescription = stringResource(R.string.cd_add_shot),
        )
    }
}

@Preview(showBackground = true, name = "Expanded")
@Composable
private fun ContextActionsToolbarExpandedPreview() {
    EspressoAppTheme {
        ContextActionsToolbar(
            expanded = true,
            items = listOf(
                FloatingNavItem(Icons.Default.Edit, R.string.cd_edit),
                FloatingNavItem(Icons.Default.Delete, R.string.cd_delete),
                FloatingNavItem(Icons.Default.ContentCopy, R.string.cd_copy),
                FloatingNavItem(Icons.Default.FilterAlt, R.string.cd_filter_by_coffee),
            ),
            onFabClick = {},
            collapsedFabIcon = Icons.Default.Add,
            collapsedFabContentDescription = stringResource(R.string.cd_add_shot),
        )
    }
}
