package org.juba.espressoapp.ui.coffee.coffeebean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import org.juba.espressoapp.extensions.duplicate
import org.juba.espressoapp.ui.filter.ShotFilter
import org.juba.espressoapp.ui.filter.ShotFilterStore
import javax.inject.Inject

@HiltViewModel
class CoffeeBeanListViewModel @Inject constructor(
    private val repository: CoffeeBeanRepository,
    private val filterStore: ShotFilterStore,
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(CoffeeBeanSortOrder.ROAST_DATE_DESC)

    private val _events = Channel<CoffeeBeanListEvent>(Channel.BUFFERED)

    /** One-shot events for copy confirmation and action failures. */
    val events: Flow<CoffeeBeanListEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<CoffeeBeanListUiState> = combine(
        repository.getAll(),
        _sortOrder,
    ) { beans, sortOrder ->
        CoffeeBeanListUiState.Success(beans.sortedBy(sortOrder)) as CoffeeBeanListUiState
    }
        .catch { emit(CoffeeBeanListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoffeeBeanListUiState.Loading,
        )

    fun setSortOrder(sortOrder: CoffeeBeanSortOrder) {
        _sortOrder.value = sortOrder
    }

    /**
     * Applies a coffee filter to the shot list.
     *
     * The filter is stored outside this screen so the Shots tab can pick it up when it becomes
     * visible.
     */
    fun setShotFilter(bean: CoffeeBean) {
        filterStore.set(
            ShotFilter.CoffeeBean(
                coffeeBeanId = bean.id,
                label = bean.name,
            ),
        )
    }

    /** Soft-deletes the bean and emits [CoffeeBeanListEvent.ActionFailed] on error. */
    fun deleteBean(id: String) {
        viewModelScope.launch {
            val result = repository.delete(id)
            if (result.isFailure) _events.send(CoffeeBeanListEvent.ActionFailed)
        }
    }

    /**
     * Creates a copy of [bean] with [roastDate] as its roast date.
     *
     * Emits [CoffeeBeanListEvent.Copied] on success or [CoffeeBeanListEvent.ActionFailed] on error.
     */
    fun copyBean(bean: CoffeeBean, roastDate: Long?) {
        viewModelScope.launch {
            val result = repository.insert(bean.duplicate(roastDate))
            _events.send(
                if (result.isSuccess) CoffeeBeanListEvent.Copied else CoffeeBeanListEvent.ActionFailed,
            )
        }
    }
}

/** Sorts the list by [order], with null roast dates always placed last. */
private fun List<CoffeeBean>.sortedBy(order: CoffeeBeanSortOrder): List<CoffeeBean> =
    when (order) {
        CoffeeBeanSortOrder.ROAST_DATE_DESC -> sortedWith(compareByDescending { it.roastDate })
        CoffeeBeanSortOrder.ROAST_DATE_ASC -> sortedWith(
            compareBy(nullsLast(naturalOrder<Long>())) { it.roastDate },
        )
    }
