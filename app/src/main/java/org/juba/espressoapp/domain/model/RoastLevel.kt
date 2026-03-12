package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined roast level options used in the coffee bean form. */
object RoastLevel {
    val options: List<SelectionOption> = listOf(
        SelectionOption("Light", "Light"),
        SelectionOption("Medium-Light", "Medium-Light"),
        SelectionOption("Medium", "Medium"),
        SelectionOption("Medium-Dark", "Medium-Dark"),
        SelectionOption("Dark", "Dark"),
    )
}
