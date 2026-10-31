package org.juba.espressoapp.ui.shots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.model.ShotLog
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import org.juba.espressoapp.domain.repository.GrinderRepository
import org.juba.espressoapp.domain.repository.ShotLogRepository
import org.juba.espressoapp.extensions.duplicate
import org.juba.espressoapp.ui.filter.ShotFilter
import org.juba.espressoapp.ui.filter.ShotFilterStore
import org.juba.espressoapp.ui.filter.filteredBy
import javax.inject.Inject

@HiltViewModel
class ShotLogListViewModel @Inject constructor(
    private val shotLogRepository: ShotLogRepository,
    coffeeBeanRepository: CoffeeBeanRepository,
    grinderRepository: GrinderRepository,
    espressoMachineRepository: EspressoMachineRepository,
    filterBasketRepository: FilterBasketRepository,
    private val filterStore: ShotFilterStore,
) : ViewModel() {

    private val _events = Channel<ShotListEvent>(Channel.BUFFERED)

    /** One-shot events for copy confirmation and action failures. */
    val events: Flow<ShotListEvent> = _events.receiveAsFlow()

    private val shotListData = combine(
        shotLogRepository.getAll(),
        coffeeBeanRepository.getAll(),
        grinderRepository.getAll(),
        espressoMachineRepository.getAll(),
        filterBasketRepository.getAll(),
    ) { shots, beans, grinders, machines, baskets ->
        ShotListData(
            shots = shots,
            beans = beans,
            canAddShot = beans.isNotEmpty() && grinders.isNotEmpty() &&
                machines.isNotEmpty() && baskets.isNotEmpty(),
        )
    }

    val uiState: StateFlow<ShotLogListUiState> = combine(
        shotListData,
        filterStore.filter,
    ) { data, filter ->
        ShotLogListUiState.Success(
            shots = data.shots.filteredBy(filter, data.beans),
            canAddShot = data.canAddShot,
            activeFilter = filter,
            totalShotCount = data.shots.size,
        ) as ShotLogListUiState
    }
        .catch { emit(ShotLogListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ShotLogListUiState.Loading,
        )

    /** Replaces the active filter. */
    fun setFilter(filter: ShotFilter) {
        filterStore.set(filter)
    }

    /** Removes the active filter. */
    fun clearFilter() {
        filterStore.clear()
    }

    /** Soft-deletes the shot and emits [ShotListEvent.ActionFailed] on error. */
    fun deleteShot(id: String) {
        viewModelScope.launch {
            val result = shotLogRepository.delete(id)
            if (result.isFailure) _events.send(ShotListEvent.ActionFailed)
        }
    }

    /**
     * Creates a copy of [shot] with [newShotAt] as its date.
     *
     * Emits [ShotListEvent.Copied] on success or [ShotListEvent.ActionFailed] on error.
     */
    fun copyShot(shot: ShotLog, newShotAt: Long) {
        viewModelScope.launch {
            val result = shotLogRepository.insert(shot.duplicate(newShotAt))
            _events.send(
                if (result.isSuccess) ShotListEvent.Copied else ShotListEvent.ActionFailed,
            )
        }
    }

    private data class ShotListData(
        val shots: List<ShotLog>,
        val beans: List<CoffeeBean>,
        val canAddShot: Boolean,
    )
}
