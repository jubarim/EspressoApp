package org.juba.espressoapp.ui.theme

import androidx.compose.ui.graphics.Color

// === Espresso (Primary) ===
val EspressoBase = Color(0xFF6B3D2E)       // medium-dark roast
val EspressoOnLight = Color(0xFFFFB59A)    // crema on dark bg
val EspressoDark = Color(0xFF3E160A)       // ristretto
val EspressoContainer = Color(0xFFFFDBC8)  // steamed milk

// === Roasted Malt (Secondary) ===
val MaltBase = Color(0xFF7B5847)
val MaltOnLight = Color(0xFFEFBDA9)
val MaltDark = Color(0xFF4A281B)
val MaltContainer = Color(0xFFFFDBC8)

// === Crema / Caramel (Tertiary) ===
val CremaBase = Color(0xFF6C5D2F)
val CremaOnLight = Color(0xFFD9C58D)
val CremaDark = Color(0xFF3B2F05)
val CremaContainer = Color(0xFFF6E1A8)

// === Surfaces ===
val FoamWhite = Color(0xFFFFF8F5)          // warm off-white
val EspressoNight = Color(0xFF1A100D)      // dark background
val EspressoSurfaceBright = Color(0xFF422D29) // dark surface bright
val WarmBeige = Color(0xFFF4DDD5)          // light surface variant
val WarmMauve = Color(0xFF53433F)          // dark surface variant / outline

// === Inverse ===
val InverseDark = Color(0xFF392926)
val InverseLight = Color(0xFFF0DED9)

// === Shot list basket size badges ===
// Container and content pairs for the ratio badge, one per basket size class.
val SingleBasketContainerLight = Color(0xFFF6E1A8) // crema gold
val OnSingleBasketContainerLight = Color(0xFF231B00)
val DoubleBasketContainerLight = Color(0xFFFFDBC8) // steamed milk
val OnDoubleBasketContainerLight = Color(0xFF260C02)
val TripleBasketContainerLight = Color(0xFFC7D8BE) // green bean
val OnTripleBasketContainerLight = Color(0xFF1F2B19)
val UnsetBasketContainerLight = WarmBeige
val OnUnsetBasketContainerLight = WarmMauve

val SingleBasketContainerDark = Color(0xFF534519) // dark crema
val OnSingleBasketContainerDark = Color(0xFFF6E1A8)
val DoubleBasketContainerDark = Color(0xFF572717) // dark espresso
val OnDoubleBasketContainerDark = Color(0xFFFFDBC8)
val TripleBasketContainerDark = Color(0xFF37452F) // dark green bean
val OnTripleBasketContainerDark = Color(0xFFCFE0C6)
val UnsetBasketContainerDark = EspressoSurfaceBright
val OnUnsetBasketContainerDark = Color(0xFFD8BDB8)
