package org.juba.espressoapp.ui.main

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Bottom padding consumed by the floating nav bar, provided at the root of [EspressoApp]. */
val LocalNavBarPadding = compositionLocalOf<Dp> { 0.dp }
