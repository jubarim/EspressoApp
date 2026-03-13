package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined pump / pressure source options used in the espresso machine form. */
object PumpType {
    val options: List<SelectionOption> = listOf(
        SelectionOption("Vibratory", "Vibratory"),
        SelectionOption("Rotary", "Rotary"),
        SelectionOption("Direct Lever", "Direct Lever"),
        SelectionOption("Spring Lever", "Spring Lever"),
    )
}
