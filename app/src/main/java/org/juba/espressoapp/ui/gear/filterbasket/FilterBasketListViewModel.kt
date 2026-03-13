package org.juba.espressoapp.ui.gear.filterbasket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import javax.inject.Inject

@HiltViewModel
class FilterBasketListViewModel @Inject constructor(
    repository: FilterBasketRepository,
) : ViewModel() {

    val uiState: StateFlow<FilterBasketListUiState> = repository
        .getAll()
        .map<_, FilterBasketListUiState> { FilterBasketListUiState.Success(it) }
        .catch { emit(FilterBasketListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FilterBasketListUiState.Loading,
        )
}
