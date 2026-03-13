package org.juba.espressoapp.ui.gear.filterbasket

import org.juba.espressoapp.domain.model.FilterBasket

sealed interface FilterBasketDetailUiState {
    data object Loading : FilterBasketDetailUiState
    data class Success(val basket: FilterBasket) : FilterBasketDetailUiState
    data object Deleted : FilterBasketDetailUiState
    data class Error(val message: String) : FilterBasketDetailUiState
}
