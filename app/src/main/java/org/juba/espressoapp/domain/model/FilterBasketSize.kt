package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/**
 * Predefined filter basket size classes. Stored as [SINGLE], [DOUBLE] or [TRIPLE] in the
 * `filter_baskets.size_class` column and used to colour the shot list ratio badge.
 */
object FilterBasketSize {
    const val SINGLE = "Single"
    const val DOUBLE = "Double"
    const val TRIPLE = "Triple"

    val options: List<SelectionOption> = listOf(
        SelectionOption(SINGLE, SINGLE),
        SelectionOption(DOUBLE, DOUBLE),
        SelectionOption(TRIPLE, TRIPLE),
    )
}
