package org.juba.espressoapp.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * Read-only outlined card that acts as a picker trigger. Displays a [label] above
 * the currently selected value (or a placeholder) and a trailing arrow icon.
 * Tap to invoke [onClick] and open the associated picker UI (e.g. [SelectionBottomSheet]).
 *
 * @param errorMessage When non-null, renders the card border in error color and shows the message below.
 */
@Composable
fun PickerField(
    label: String,
    selectedLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = if (errorMessage != null) {
                androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            } else {
                CardDefaults.outlinedCardBorder()
            },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (errorMessage != null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = selectedLabel ?: stringResource(R.string.picker_no_selection),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedLabel != null) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PickerFieldPreview() {
    EspressoAppTheme {
        PickerField(
            label = "Country",
            selectedLabel = "Brazil",
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PickerFieldEmptyPreview() {
    EspressoAppTheme {
        PickerField(
            label = "Country",
            selectedLabel = null,
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PickerFieldErrorPreview() {
    EspressoAppTheme {
        PickerField(
            label = "Country",
            selectedLabel = null,
            onClick = {},
            errorMessage = "Enter a valid country"
        )
    }
}