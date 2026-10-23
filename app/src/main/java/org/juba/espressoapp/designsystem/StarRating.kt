package org.juba.espressoapp.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.juba.espressoapp.R
import org.juba.espressoapp.ui.theme.EspressoAppTheme

/** Direction in which [StarRating] lays its stars out. */
enum class StarRatingOrientation { Horizontal, Vertical }

private const val DEFAULT_MAX_STARS = 5

/**
 * Shows [rating] as a row or column of [maxStars] stars, filling them from the start and
 * leaving the rest outlined. Values outside `0..maxStars` are clamped.
 *
 * Horizontal stars fill from the start (left in LTR). Vertical stars fill from the bottom,
 * so the stack reads as a level that grows upwards.
 *
 * The whole group is exposed to accessibility as a single node with a "rating out of"
 * description, the individual icons are decorative.
 *
 * @param rating Number of filled stars.
 * @param maxStars Total number of stars drawn, filled plus empty.
 * @param starSize Size of each star icon.
 * @param spacing Gap between stars.
 * @param color Colour of the filled stars. Defaults to the current content colour, so the
 *   component adapts to whatever it is placed on.
 * @param emptyStarAlpha Alpha applied to the outlined stars.
 */
@Composable
fun StarRating(
    rating: Int,
    modifier: Modifier = Modifier,
    orientation: StarRatingOrientation = StarRatingOrientation.Horizontal,
    starSize: Dp = 20.dp,
    spacing: Dp = 2.dp,
    maxStars: Int = DEFAULT_MAX_STARS,
    color: Color = LocalContentColor.current,
    emptyStarAlpha: Float = 0.3f,
) {
    val filledCount = rating.coerceIn(0, maxStars)
    val description = stringResource(R.string.cd_star_rating, filledCount, maxStars)
    val ratingModifier = modifier.semantics(mergeDescendants = true) {
        contentDescription = description
    }

    when (orientation) {
        StarRatingOrientation.Horizontal -> Row(
            modifier = ratingModifier,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(maxStars) { index ->
                StarIcon(
                    filled = index < filledCount,
                    starSize = starSize,
                    color = color,
                    emptyStarAlpha = emptyStarAlpha,
                )
            }
        }

        StarRatingOrientation.Vertical -> Column(
            modifier = ratingModifier,
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(maxStars) { index ->
                StarIcon(
                    filled = index >= maxStars - filledCount,
                    starSize = starSize,
                    color = color,
                    emptyStarAlpha = emptyStarAlpha,
                )
            }
        }
    }
}

@Composable
private fun StarIcon(
    filled: Boolean,
    starSize: Dp,
    color: Color,
    emptyStarAlpha: Float,
) {
    Icon(
        imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
        contentDescription = null,
        tint = if (filled) color else color.copy(alpha = emptyStarAlpha),
        modifier = Modifier.size(starSize),
    )
}

@Preview(showBackground = true, name = "Horizontal")
@Composable
private fun StarRatingHorizontalPreview() {
    EspressoAppTheme {
        StarRating(rating = 3)
    }
}

@Preview(showBackground = true, name = "Vertical")
@Composable
private fun StarRatingVerticalPreview() {
    EspressoAppTheme {
        StarRating(rating = 3, orientation = StarRatingOrientation.Vertical)
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun StarRatingEmptyPreview() {
    EspressoAppTheme {
        StarRating(rating = 0)
    }
}
