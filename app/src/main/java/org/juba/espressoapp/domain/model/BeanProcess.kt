package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined espresso bean processing methods used in the coffee bean form. */
object BeanProcess {
    val options: List<SelectionOption> = listOf(
        SelectionOption("Washed", "Washed"),
        SelectionOption("Natural", "Natural"),
        SelectionOption("Honey", "Honey"),
        SelectionOption("Anaerobic", "Anaerobic"),
        SelectionOption("Wet-Hulled", "Wet-Hulled"),
    )
}
