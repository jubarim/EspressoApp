package org.juba.espressoapp.ui.filter

/**
 * A filter applied to the shot list.
 *
 * [label] is the display name captured when the filter is applied. It is shown in the active filter
 * chip and can become stale if the filtered record is renamed while the filter is active.
 */
sealed interface ShotFilter {

    val label: String

    /** Matches shots logged with the exact [coffeeBeanId]. */
    data class CoffeeBean(
        val coffeeBeanId: String,
        override val label: String,
    ) : ShotFilter

    /** Matches shots whose coffee bean belongs to [roasterId]. */
    data class Roaster(
        val roasterId: String,
        override val label: String,
    ) : ShotFilter
}
