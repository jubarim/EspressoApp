package org.juba.espressoapp.ui.coffee.coffeebean

import org.juba.espressoapp.domain.model.CoffeeBean

sealed interface CoffeeBeanDetailUiState {
    data object Loading : CoffeeBeanDetailUiState
    data class Success(val bean: CoffeeBean) : CoffeeBeanDetailUiState
    data object Deleted : CoffeeBeanDetailUiState
    data class Error(val message: String) : CoffeeBeanDetailUiState
}
