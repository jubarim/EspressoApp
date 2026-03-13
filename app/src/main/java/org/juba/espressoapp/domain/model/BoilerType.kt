package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined boiler type options used in the espresso machine form. */
object BoilerType {
    val options: List<SelectionOption> = listOf(
        SelectionOption("Single Boiler", "Single Boiler"),
        SelectionOption("HX", "Heat Exchanger (HX)"),
        SelectionOption("Dual Boiler", "Dual Boiler"),
        SelectionOption("Thermoblock", "Thermoblock"),
        SelectionOption("No Boiler", "No Boiler"),
    )
}
