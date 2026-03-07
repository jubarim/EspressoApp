package org.juba.espressoapp.ui.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.designsystem.input.SearchTextField
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * Generic searchable bottom sheet for selecting one or more [SelectionOption]s.
 *
 * In single-select mode ([multiSelect] = false) each row tap immediately calls [onConfirm]
 * with a singleton set and then [onDismiss]. In multi-select mode a "Confirm" button
 * appears at the bottom; [onConfirm] is called only when the button is tapped.
 *
 * @param title Sheet heading displayed above the search bar.
 * @param options Full list of selectable options.
 * @param selectedKeys Keys that should appear pre-selected when the sheet opens.
 * @param onConfirm Called with the final set of selected keys.
 * @param onDismiss Called to close the sheet (both on selection and on dismiss gesture).
 * @param multiSelect When true, checkboxes are shown and a Confirm button is required.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionBottomSheet(
    title: String,
    options: List<SelectionOption>,
    selectedKeys: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    multiSelect: Boolean = false,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        SelectionBottomSheetContent(
            title = title,
            options = options,
            selectedKeys = selectedKeys,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            multiSelect = multiSelect,
        )
    }
}

@Composable
private fun SelectionBottomSheetContent(
    title: String,
    options: List<SelectionOption>,
    selectedKeys: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    multiSelect: Boolean,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    var pendingKeys by remember { mutableStateOf(selectedKeys) }

    val filteredOptions = remember(options, searchQuery) {
        if (searchQuery.isBlank()) options
        else options.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        HorizontalDivider()

        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSearchTrigger = { searchQuery = it },
            searchPlaceHolder = stringResource(R.string.picker_search_placeholder),
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredOptions, key = { it.key }) { option ->
                if (multiSelect) {
                    ListItem(
                        headlineContent = { Text(option.label) },
                        trailingContent = {
                            Checkbox(
                                checked = option.key in pendingKeys,
                                onCheckedChange = { checked ->
                                    pendingKeys = if (checked) {
                                        pendingKeys + option.key
                                    } else {
                                        pendingKeys - option.key
                                    }
                                },
                            )
                        },
                    )
                } else {
                    ListItem(
                        headlineContent = { Text(option.label) },
                        modifier = Modifier.clickable {
                            onConfirm(setOf(option.key))
                            onDismiss()
                        },
                    )
                }
            }
        }

        if (multiSelect) {
            Button(
                onClick = {
                    onConfirm(pendingKeys)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(stringResource(R.string.picker_confirm))
            }
        }
    }
}

private val PREVIEW_OPTIONS = listOf(
    SelectionOption("BR", "Brazil"),
    SelectionOption("DE", "Germany"),
    SelectionOption("IT", "Italy"),
    SelectionOption("JP", "Japan"),
    SelectionOption("NO", "Norway"),
    SelectionOption("US", "United States of America"),
)

@Preview(showBackground = true)
@Composable
private fun SelectionBottomSheetSinglePreview() {
    EspressoAppTheme {
        SelectionBottomSheetContent(
            title = "Country",
            options = PREVIEW_OPTIONS,
            selectedKeys = setOf("IT"),
            onConfirm = {},
            onDismiss = {},
            multiSelect = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectionBottomSheetMultiPreview() {
    EspressoAppTheme {
        SelectionBottomSheetContent(
            title = "Process",
            options = listOf(
                SelectionOption("washed", "Washed"),
                SelectionOption("natural", "Natural"),
                SelectionOption("honey", "Honey"),
                SelectionOption("anaerobic", "Anaerobic"),
                SelectionOption("wet_hulled", "Wet Hulled"),
            ),
            selectedKeys = setOf("washed", "honey"),
            onConfirm = {},
            onDismiss = {},
            multiSelect = true,
        )
    }
}