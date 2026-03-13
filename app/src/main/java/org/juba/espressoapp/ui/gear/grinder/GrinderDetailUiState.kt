package org.juba.espressoapp.ui.gear.grinder

import org.juba.espressoapp.domain.model.Grinder

sealed interface GrinderDetailUiState {
    data object Loading : GrinderDetailUiState
    data class Success(val grinder: Grinder) : GrinderDetailUiState
    data object Deleted : GrinderDetailUiState
    data class Error(val message: String) : GrinderDetailUiState
}
