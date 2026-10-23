package org.juba.espressoapp.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.juba.espressoapp.domain.model.FilterBasketSize

/** Container and content colours for one basket size class. */
@Immutable
data class BasketBadgeColorPair(
    val container: Color,
    val content: Color,
)

/**
 * Container and content colours used by the shot list ratio badge and the shot detail banner
 * to encode the filter basket size class. Defined outside the Material
 * [androidx.compose.material3.ColorScheme] because they are app specific semantic colours,
 * not Material roles.
 */
@Immutable
data class BasketBadgeColors(
    val singleContainer: Color,
    val onSingleContainer: Color,
    val doubleContainer: Color,
    val onDoubleContainer: Color,
    val tripleContainer: Color,
    val onTripleContainer: Color,
    val unsetContainer: Color,
    val onUnsetContainer: Color,
) {
    /** Colours matching [sizeClass], falling back to the neutral pair when it is null or unknown. */
    fun colorPairFor(sizeClass: String?): BasketBadgeColorPair = when (sizeClass) {
        FilterBasketSize.SINGLE -> BasketBadgeColorPair(singleContainer, onSingleContainer)
        FilterBasketSize.DOUBLE -> BasketBadgeColorPair(doubleContainer, onDoubleContainer)
        FilterBasketSize.TRIPLE -> BasketBadgeColorPair(tripleContainer, onTripleContainer)
        else -> BasketBadgeColorPair(unsetContainer, onUnsetContainer)
    }
}

internal val LightBasketBadgeColors = BasketBadgeColors(
    singleContainer = SingleBasketContainerLight,
    onSingleContainer = OnSingleBasketContainerLight,
    doubleContainer = DoubleBasketContainerLight,
    onDoubleContainer = OnDoubleBasketContainerLight,
    tripleContainer = TripleBasketContainerLight,
    onTripleContainer = OnTripleBasketContainerLight,
    unsetContainer = UnsetBasketContainerLight,
    onUnsetContainer = OnUnsetBasketContainerLight,
)

internal val DarkBasketBadgeColors = BasketBadgeColors(
    singleContainer = SingleBasketContainerDark,
    onSingleContainer = OnSingleBasketContainerDark,
    doubleContainer = DoubleBasketContainerDark,
    onDoubleContainer = OnDoubleBasketContainerDark,
    tripleContainer = TripleBasketContainerDark,
    onTripleContainer = OnTripleBasketContainerDark,
    unsetContainer = UnsetBasketContainerDark,
    onUnsetContainer = OnUnsetBasketContainerDark,
)

/** Badge colours for the current theme, provided by [EspressoAppTheme]. */
val LocalBasketBadgeColors = staticCompositionLocalOf { LightBasketBadgeColors }
