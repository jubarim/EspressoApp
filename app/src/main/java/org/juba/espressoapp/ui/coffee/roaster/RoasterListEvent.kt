package org.juba.espressoapp.ui.coffee.roaster

/** One-shot events emitted by [RoasterListViewModel] for the screen to present. */
sealed interface RoasterListEvent {
    data object ActionFailed : RoasterListEvent
}
