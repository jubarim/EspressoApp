package org.juba.espressoapp.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * Chip that shows the active list filter and clears it when tapped.
 *
 * @param label Visible filter name, usually the filtered record's name.
 * @param contentDescription Full accessibility description, for example "Filtered by coffee
 *   Ethiopia. Tap to clear."
 * @param onClear Invoked when the chip is tapped.
 * @param modifier Modifier applied to the chip.
 * @param icon Optional leading icon that identifies the filter type.
 */
@Composable
fun ActiveFilterChip(
    label: String,
    contentDescription: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    InputChip(
        selected = true,
        onClick = onClear,
        label = { Text(label) },
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
            )
        },
        modifier = modifier.semantics { this.contentDescription = contentDescription },
    )
}

@Preview(showBackground = true)
@Composable
private fun ActiveFilterChipPreview() {
    EspressoAppTheme {
        ActiveFilterChip(
            label = "Ethiopia Yirgacheffe",
            contentDescription = "Filtered by coffee Ethiopia Yirgacheffe. Tap to clear.",
            onClear = {},
            icon = Icons.Default.Coffee,
        )
    }
}
