package org.juba.espressoapp.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * Styled [OutlinedTextField] with a consistent label and optional error message.
 * Use for all entity form screens to ensure a uniform look across the app.
 */
@Composable
fun EspressoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            { Text(errorMessage) }
        } else {
            null
        },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun EspressoTextFieldPreview() {
    EspressoAppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            EspressoTextField(
                value = "Ethiopia Yirgacheffe",
                onValueChange = {},
                label = "Name",
            )
            EspressoTextField(
                value = "",
                onValueChange = {},
                label = "Name",
            )
            EspressoTextField(
                value = "bad value",
                onValueChange = {},
                label = "Name",
                errorMessage = "Name is required",
            )
        }
    }
}
