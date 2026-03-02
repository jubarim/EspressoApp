package org.juba.espressoapp.ui.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
