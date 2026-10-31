package org.juba.espressoapp.ui.shots

/** One-shot events emitted by [ShotLogListViewModel] for the screen to present. */
sealed interface ShotListEvent {
    data object Copied : ShotListEvent
    data object ActionFailed : ShotListEvent
}
