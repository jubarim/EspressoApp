package org.juba.espressoapp.ui.shots

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
fun ShotsScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Shots") }) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text("No shots logged yet.")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShotsScreenPreview() {
    EspressoAppTheme {
        ShotsScreen()
    }
}
