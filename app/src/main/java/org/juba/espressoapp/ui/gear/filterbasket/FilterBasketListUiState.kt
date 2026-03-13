package org.juba.espressoapp.ui.gear.filterbasket

import org.juba.espressoapp.domain.model.FilterBasket

sealed interface FilterBasketListUiState {
    data object Loading : FilterBasketListUiState
    data class Success(val baskets: List<FilterBasket>) : FilterBasketListUiState
    data class Error(val message: String) : FilterBasketListUiState
}
