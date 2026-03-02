package org.juba.espressoapp.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.juba.espressoapp.ui.theme.EspressoAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text("Settings — coming soon.")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    EspressoAppTheme {
        SettingsScreen()
    }
}
