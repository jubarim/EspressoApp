package org.juba.espressoapp.ui.designsystem.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.ui.theme.EspressoAppTheme

@Composable
fun SearchTextField(
    searchQuery: String,
    searchPlaceHolder: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchTrigger: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        focusManager.clearFocus()
        onSearchTrigger(searchQuery)
    }

    OutlinedTextField(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "",
            )
        },
        trailingIcon = if (searchQuery.isNotEmpty()) {
            {
                IconButton(
                    onClick = {
                        onSearchQueryChange("")
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                    )
                }
            }
        } else {
            null
        },
        onValueChange = {
            if ("\n" !in it) onSearchQueryChange(it)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            Text(
                text = searchPlaceHolder,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .testTag("searchTextField"),
        shape = CircleShape,
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchTextFieldPreview() {
    EspressoAppTheme {
        Column {
            SearchTextField(
                modifier = Modifier.padding(8.dp),
                searchQuery = "",
                onSearchQueryChange = {},
                onSearchTrigger = {},
                searchPlaceHolder = "Search something"
            )

            SearchTextField(
                modifier = Modifier.padding(8.dp),
                searchQuery = "Typed something",
                onSearchQueryChange = {},
                onSearchTrigger = {},
                searchPlaceHolder = "Search something"
            )
        }
    }
}