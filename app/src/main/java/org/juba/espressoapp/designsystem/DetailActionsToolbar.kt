package org.juba.espressoapp.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * A floating toolbar for detail screens with Edit, Delete, and an optional Copy action.
 * Positioning is left to the caller via [modifier].
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailActionsToolbar(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onCopy: (() -> Unit)? = null,
) {
    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier,
    ) {
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.cd_edit))
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cd_delete))
        }
        if (onCopy != null) {
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.cd_copy))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailActionsToolbarPreview() {
    EspressoAppTheme {
        DetailActionsToolbar(
            onEdit = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailActionsToolbarWithCopyPreview() {
    EspressoAppTheme {
        DetailActionsToolbar(
            onEdit = {},
            onDelete = {},
            onCopy = {},
        )
    }
}
