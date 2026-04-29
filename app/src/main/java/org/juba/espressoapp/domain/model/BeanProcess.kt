package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined espresso bean processing methods used in the coffee bean form. */
object BeanProcess {
    val options: List<SelectionOption> = listOf(
        SelectionOption("Washed", "Washed / Fully Washed"),
        SelectionOption("Semi-Washed", "Semi-Washed (Despolpado)"),
        SelectionOption("PulpedNatural", "Pulped Natural (CD)"),
        SelectionOption("Honey", "Honey Process"),
        SelectionOption("Natural", "Natural (Dry Process)"),
        SelectionOption("Anaerobic", "Anaerobic Fermentation"),
        SelectionOption("WetHulled", "Wet-Hulled (Giling Basah)"),
    )
}
