package org.juba.espressoapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    // Primary — crema glow on deep espresso background
    primary = EspressoOnLight,
    onPrimary = EspressoDark,
    primaryContainer = Color(0xFF572717),
    onPrimaryContainer = EspressoContainer,
    // Secondary — roasted malt tones
    secondary = MaltOnLight,
    onSecondary = MaltDark,
    secondaryContainer = Color(0xFF643D2F),
    onSecondaryContainer = MaltContainer,
    // Tertiary — golden crema accent
    tertiary = CremaOnLight,
    onTertiary = CremaDark,
    tertiaryContainer = Color(0xFF534519),
    onTertiaryContainer = CremaContainer,
    // Surfaces — coffee shop at night
    background = EspressoNight,
    onBackground = InverseLight,
    surface = EspressoNight,
    onSurface = InverseLight,
    surfaceVariant = WarmMauve,
    onSurfaceVariant = Color(0xFFD8BDB8),
    surfaceBright = EspressoSurfaceBright,
    outline = Color(0xFFA08580),
    outlineVariant = WarmMauve,
    inverseSurface = InverseLight,
    inverseOnSurface = InverseDark,
    inversePrimary = EspressoBase,
)

private val LightColorScheme = lightColorScheme(
    // Primary — espresso brown
    primary = EspressoBase,
    onPrimary = Color.White,
    primaryContainer = EspressoContainer,
    onPrimaryContainer = Color(0xFF260C02),
    // Secondary — roasted malt
    secondary = MaltBase,
    onSecondary = Color.White,
    secondaryContainer = MaltContainer,
    onSecondaryContainer = Color(0xFF31160A),
    // Tertiary — caramel / golden crema
    tertiary = CremaBase,
    onTertiary = Color.White,
    tertiaryContainer = CremaContainer,
    onTertiaryContainer = Color(0xFF231B00),
    // Surfaces — milk foam warmth
    background = FoamWhite,
    onBackground = Color(0xFF221210),
    surface = FoamWhite,
    onSurface = Color(0xFF221210),
    surfaceVariant = WarmBeige,
    onSurfaceVariant = WarmMauve,
    outline = Color(0xFF857370),
    outlineVariant = Color(0xFFD8BDB8),
    inverseSurface = InverseDark,
    inverseOnSurface = Color(0xFFFFEDE9),
    inversePrimary = EspressoOnLight,
)

@Composable
fun EspressoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}