package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined basket type options used in the filter basket form. */
object FilterBasketType {
    val options: List<SelectionOption> = listOf(
        SelectionOption("Ridged", "Ridged"),
        SelectionOption("Ridgeless", "Ridgeless"),
        SelectionOption("Precision", "Precision"),
    )
}
