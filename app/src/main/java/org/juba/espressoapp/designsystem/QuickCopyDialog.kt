package org.juba.espressoapp.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.juba.espressoapp.R
import org.juba.espressoapp.extensions.epochToLocalTimeZoneConvertor
import org.juba.espressoapp.ui.theme.EspressoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Reusable dialog for duplicating a record with a new date.
 *
 * @param title Dialog title, e.g. "Copy Shot".
 * @param dateFieldLabel Label shown above the date picker card.
 * @param noDatePlaceholder Text shown in the date card when no date is selected.
 * @param initialDate The original record's date — Create is enabled only when the selected date
 *   differs from this value. Pass `null` to keep Create always enabled.
 * @param defaultPickerDate The date pre-selected in the picker on first open.
 * @param onDismiss Called when the dialog is dismissed without action.
 * @param onCustomize Called when the user wants to open the full edit form pre-populated.
 * @param onCreate Called with the selected date when the user confirms a quick copy.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCopyDialog(
    title: String,
    dateFieldLabel: String,
    noDatePlaceholder: String,
    initialDate: Long?,
    defaultPickerDate: Long?,
    onDismiss: () -> Unit,
    onCustomize: () -> Unit,
    onCreate: (Long?) -> Unit,
) {
    var selectedDate by remember { mutableStateOf(defaultPickerDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
    val dateLabel = selectedDate?.let { dateFormatter.format(Date(it)) } ?: noDatePlaceholder

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = dateFieldLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedCard(onClick = { showDatePicker = true }) {
                        Text(
                            text = dateLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    TextButton(onClick = onCustomize) {
                        Text(stringResource(R.string.action_customize))
                    }
                    TextButton(
                        onClick = { onCreate(selectedDate) },
                        enabled = selectedDate != initialDate,
                    ) {
                        Text(stringResource(R.string.action_create))
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis?.epochToLocalTimeZoneConvertor()
                    showDatePicker = false
                }) { Text(stringResource(R.string.picker_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickCopyDialogWithDatePreview() {
    EspressoAppTheme {
        QuickCopyDialog(
            title = "Copy Shot",
            dateFieldLabel = "Shot date",
            noDatePlaceholder = "",
            initialDate = null,
            defaultPickerDate = 1_746_057_600_000L,
            onDismiss = {},
            onCustomize = {},
            onCreate = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickCopyDialogNoDatePreview() {
    EspressoAppTheme {
        QuickCopyDialog(
            title = "Copy Coffee Bean",
            dateFieldLabel = "Roast date",
            noDatePlaceholder = "No roast date set",
            initialDate = null,
            defaultPickerDate = null,
            onDismiss = {},
            onCustomize = {},
            onCreate = {},
        )
    }
}
