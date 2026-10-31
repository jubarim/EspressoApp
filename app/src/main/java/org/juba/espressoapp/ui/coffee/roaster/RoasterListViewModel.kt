package org.juba.espressoapp.ui.coffee.roaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.model.Roaster
import org.juba.espressoapp.domain.repository.RoasterRepository
import org.juba.espressoapp.ui.filter.ShotFilter
import org.juba.espressoapp.ui.filter.ShotFilterStore
import javax.inject.Inject

@HiltViewModel
class RoasterListViewModel @Inject constructor(
    private val repository: RoasterRepository,
    private val filterStore: ShotFilterStore,
) : ViewModel() {

    private val _events = Channel<RoasterListEvent>(Channel.BUFFERED)

    /** One-shot events for action failures. */
    val events: Flow<RoasterListEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<RoasterListUiState> = repository
        .getAll()
        .map<_, RoasterListUiState> { RoasterListUiState.Success(it) }
        .catch { emit(RoasterListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RoasterListUiState.Loading,
        )

    /**
     * Applies a roaster filter to the shot list.
     *
     * The filter is stored outside this screen so the Shots tab can pick it up when it becomes
     * visible.
     */
    fun setShotFilter(roaster: Roaster) {
        filterStore.set(
            ShotFilter.Roaster(
                roasterId = roaster.id,
                label = roaster.name,
            ),
        )
    }

    /** Soft-deletes the roaster and emits [RoasterListEvent.ActionFailed] on error. */
    fun delete(id: String) {
        viewModelScope.launch {
            val result = repository.delete(id)
            if (result.isFailure) _events.send(RoasterListEvent.ActionFailed)
        }
    }
}
