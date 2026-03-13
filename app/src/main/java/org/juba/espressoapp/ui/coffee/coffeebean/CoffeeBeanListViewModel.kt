package org.juba.espressoapp.ui.coffee.coffeebean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import javax.inject.Inject

@HiltViewModel
class CoffeeBeanListViewModel @Inject constructor(
    repository: CoffeeBeanRepository,
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(CoffeeBeanSortOrder.ROAST_DATE_DESC)

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
}

/** Sorts the list by [order], with null roast dates always placed last. */
private fun List<CoffeeBean>.sortedBy(order: CoffeeBeanSortOrder): List<CoffeeBean> =
    when (order) {
        CoffeeBeanSortOrder.ROAST_DATE_DESC -> sortedWith(compareByDescending { it.roastDate })
        CoffeeBeanSortOrder.ROAST_DATE_ASC -> sortedWith(
            compareBy(nullsLast(naturalOrder<Long>())) { it.roastDate },
        )
    }
