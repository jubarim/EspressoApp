package org.juba.espressoapp.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * A tappable list row for category navigation screens (e.g. Coffee, Gear tabs).
 * Shows a [label] and calls [onClick] when tapped, followed by a divider.
 */
@Composable
fun CategoryListItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text(label) },
        modifier = modifier.clickable(onClick = onClick),
    )
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
private fun CategoryListItemPreview() {
    EspressoAppTheme {
        CategoryListItem(
            label = "Roasters",
            onClick = {},
        )
    }
}
