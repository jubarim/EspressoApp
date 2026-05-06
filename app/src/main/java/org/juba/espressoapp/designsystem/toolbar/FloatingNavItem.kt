package org.juba.espressoapp.designsystem.toolbar

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a single item in a [FloatingToolbar]-based component.
 *
 * @param icon Icon to display.
 * @param labelRes String resource for the content description (and label when shown).
 * @param onClick Action invoked when the item is tapped.
 */
data class FloatingNavItem(
    val icon: ImageVector,
    @param:StringRes val labelRes: Int,
    val onClick: () -> Unit = {},
)
