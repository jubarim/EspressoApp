package org.juba.espressoapp.ui.coffee.coffeebean

import org.juba.espressoapp.domain.model.CoffeeBean

sealed interface CoffeeBeanListUiState {
    data object Loading : CoffeeBeanListUiState
    data class Success(val beans: List<CoffeeBean>) : CoffeeBeanListUiState
    data class Error(val message: String) : CoffeeBeanListUiState
}
