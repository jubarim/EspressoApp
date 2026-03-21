package org.juba.espressoapp.ui.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

/** App-wide [SnackbarHostState] provided at the root of [EspressoApp]. */
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided — wrap your composable tree with EspressoApp")
}
