package org.juba.espressoapp.ui.coffee.roaster

import org.juba.espressoapp.domain.model.Roaster

sealed interface RoasterListUiState {
    data object Loading : RoasterListUiState
    data class Success(val roasters: List<Roaster>) : RoasterListUiState
    data class Error(val message: String) : RoasterListUiState
}
