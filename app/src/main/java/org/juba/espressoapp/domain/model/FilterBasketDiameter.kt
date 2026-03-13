package org.juba.espressoapp.domain.model

import org.juba.espressoapp.designsystem.SelectionOption

/** Predefined basket diameter options used in the filter basket form. */
object FilterBasketDiameter {
    val options: List<SelectionOption> = listOf(
        SelectionOption("46mm", "46mm"),
        SelectionOption("49mm", "49mm"),
        SelectionOption("51mm", "51mm"),
        SelectionOption("53mm", "53mm"),
        SelectionOption("54mm", "54mm"),
        SelectionOption("58mm", "58mm"),
        SelectionOption("61mm", "61mm"),
    )
}
