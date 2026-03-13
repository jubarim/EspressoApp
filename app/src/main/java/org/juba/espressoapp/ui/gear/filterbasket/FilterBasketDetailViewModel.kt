package org.juba.espressoapp.ui.gear.filterbasket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import javax.inject.Inject

@HiltViewModel
class FilterBasketDetailViewModel @Inject constructor(
    private val repository: FilterBasketRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val basketId: String = checkNotNull(savedStateHandle[FilterBasketFormViewModel.BASKET_ID])

    private val _uiState = MutableStateFlow<FilterBasketDetailUiState>(FilterBasketDetailUiState.Loading)
    val uiState: StateFlow<FilterBasketDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val basket = repository.getById(basketId)
            _uiState.update {
                if (basket != null) FilterBasketDetailUiState.Success(basket)
                else FilterBasketDetailUiState.Error("Filter basket not found")
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(basketId)
            _uiState.update { FilterBasketDetailUiState.Deleted }
        }
    }
}
