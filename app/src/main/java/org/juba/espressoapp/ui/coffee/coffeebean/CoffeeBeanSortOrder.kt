package org.juba.espressoapp.ui.coffee.coffeebean

/** Available sort orders for the coffee bean list. */
enum class CoffeeBeanSortOrder {
    /** Most recently roasted first; beans with no roast date appear last. */
    ROAST_DATE_DESC,

    /** Oldest roasted first; beans with no roast date appear last. */
    ROAST_DATE_ASC,
}