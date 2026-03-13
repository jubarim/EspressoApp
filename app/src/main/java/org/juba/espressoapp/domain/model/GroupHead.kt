package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined group head options used in the espresso machine form. */
object GroupHead {
    val options: List<SelectionOption> = listOf(
        SelectionOption("E61", "E61"),
        SelectionOption("Saturated", "Saturated"),
        SelectionOption("Semi-saturated", "Semi-saturated"),
        SelectionOption("Commercial", "Commercial"),
        SelectionOption("Lever", "Lever"),
    )
}
