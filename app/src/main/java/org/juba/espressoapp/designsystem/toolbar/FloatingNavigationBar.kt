package org.juba.espressoapp.designsystem.toolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import kotlin.math.roundToInt

/**
 * A floating pill-shaped navigation bar. Each item carries its own [FloatingNavItem.onClick]
 * callback; the selected item is highlighted by a pill that slides smoothly between items.
 *
 * **Pill animation:** a single [androidx.compose.foundation.layout.Spacer] with a
 * [androidx.compose.foundation.shape.CircleShape] background is drawn behind the items row.
 * Its `x` position and width are driven by two [androidx.compose.animation.core.Animatable]s
 * that target the selected item's measured bounds ([onGloballyPositioned]).
 * On the first measurement the values are snapped (no animation); on subsequent selection changes
 * they animate with a [androidx.compose.animation.core.tween] `FastOutSlowIn` curve (300 ms).
 * To switch to a bouncy spring or a stretchy leading-edge effect, replace the `spec` inside the
 * `LaunchedEffect` block.
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
    val itemRects = remember(items.size) { mutableStateListOf(*Array(items.size) { IntRect.Zero }) }
    var containerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val pillX = remember { Animatable(0f) }
    val pillW = remember { Animatable(0f) }
    var pillInitialized by remember { mutableStateOf(false) }

    val target = itemRects.getOrElse(selectedIndex) { IntRect.Zero }

    LaunchedEffect(target, selectedIndex) {
        if (target.width == 0) return@LaunchedEffect
        if (!pillInitialized) {
            pillX.snapTo(target.left.toFloat())
            pillW.snapTo(target.width.toFloat())
            pillInitialized = true
        } else {
            val spec = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
            launch { pillX.animateTo(target.left.toFloat(), spec) }
            launch { pillW.animateTo(target.width.toFloat(), spec) }
        }
    }

    FloatingToolbar(
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        shadowElevation = shadowElevation,
    ) {
        val density = LocalDensity.current
        Box(modifier = Modifier.onGloballyPositioned { containerCoords = it }) {
            // Pill drawn behind items — only this element animates, not the icon/label
            if (pillW.value > 0f) {
                Spacer(
                    modifier = Modifier
                        .offset { IntOffset(pillX.value.roundToInt(), 0) }
                        .size(
                            width = with(density) { pillW.value.toDp() },
                            height = with(density) { target.height.toDp() },
                        )
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items.forEachIndexed { index, item ->
                    FloatingNavItemView(
                        item = item,
                        selected = index == selectedIndex,
                        modifier = Modifier.onGloballyPositioned { coords ->
                            val container = containerCoords ?: return@onGloballyPositioned
                            val topLeft = container.localPositionOf(coords, Offset.Zero)
                            val rect = IntRect(
                                left = topLeft.x.roundToInt(),
                                top = topLeft.y.roundToInt(),
                                right = (topLeft.x + coords.size.width).roundToInt(),
                                bottom = (topLeft.y + coords.size.height).roundToInt(),
                            )
                            if (itemRects[index] != rect) itemRects[index] = rect
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavItemView(
    item: FloatingNavItem,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
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
        color = Color.Transparent,
        modifier = modifier,
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