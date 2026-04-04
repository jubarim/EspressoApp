package org.juba.espressoapp.ui.shots

import org.juba.espressoapp.domain.model.ShotLog

sealed interface ShotLogDetailUiState {
    data object Loading : ShotLogDetailUiState
    data class Success(val shot: ShotLog) : ShotLogDetailUiState
    data class Error(val message: String) : ShotLogDetailUiState
    data object Deleted : ShotLogDetailUiState
}
