package org.juba.espressoapp.ui.coffee.roaster

import org.juba.espressoapp.domain.model.Roaster

sealed interface RoasterDetailUiState {
    data object Loading : RoasterDetailUiState
    data class Success(val roaster: Roaster) : RoasterDetailUiState
    data object Deleted : RoasterDetailUiState
    data class Error(val message: String) : RoasterDetailUiState
}
