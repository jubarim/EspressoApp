package org.juba.espressoapp.ui.filter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the active shot list filter for the lifetime of the process.
 *
 * A single instance is shared so that an action outside the Shots tab, such as View shots on a
 * roaster, can apply a filter before navigating to the shot list. The filter is intentionally not
 * persisted, so a cold start always shows every shot.
 */
@Singleton
class ShotFilterStore @Inject constructor() {

    private val _filter = MutableStateFlow<ShotFilter?>(null)

    /** The current filter, or null when the list is unfiltered. */
    val filter: StateFlow<ShotFilter?> = _filter.asStateFlow()

    /** Replaces the current filter. */
    fun set(filter: ShotFilter) {
        _filter.value = filter
    }

    /** Clears the current filter. */
    fun clear() {
        _filter.value = null
    }
}
