package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined burr type options used in the grinder form. */
object BurrType {
    val options: List<SelectionOption> = listOf(
        SelectionOption("Flat", "Flat"),
        SelectionOption("Conical", "Conical"),
        SelectionOption("Hybrid", "Hybrid"),
    )
}