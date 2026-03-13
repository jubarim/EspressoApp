package org.juba.espressoapp.ui.gear.grinder

import org.juba.espressoapp.domain.model.Grinder

sealed interface GrinderListUiState {
    data object Loading : GrinderListUiState
    data class Success(val grinders: List<Grinder>) : GrinderListUiState
    data class Error(val message: String) : GrinderListUiState
}
