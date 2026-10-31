package org.juba.espressoapp.ui.coffee.coffeebean

/** One-shot events emitted by [CoffeeBeanListViewModel] for the screen to present. */
sealed interface CoffeeBeanListEvent {
    data object Copied : CoffeeBeanListEvent
    data object ActionFailed : CoffeeBeanListEvent
}
