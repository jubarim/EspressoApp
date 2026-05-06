package org.juba.espressoapp.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/**
 * A floating toolbar for detail screens with Edit, Delete, and an optional Copy action.
 * Positioning is left to the caller via [modifier].
 */
@Composable
fun DetailActionsToolbar(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onCopy: (() -> Unit)? = null,
    shape: Shape = FloatingToolbarDefaults.shape,
    containerColor: Color = FloatingToolbarDefaults.containerColor(),
    shadowElevation: Dp = FloatingToolbarDefaults.shadowElevation,
) {
    Surface(
        shape = shape,
        color = containerColor,
        shadowElevation = shadowElevation,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
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