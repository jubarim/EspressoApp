package org.juba.espressoapp.ui.shots

import org.juba.espressoapp.domain.model.ShotLog

sealed interface ShotLogListUiState {
    data object Loading : ShotLogListUiState
    data class Success(
        val shots: List<ShotLog>,
        val canAddShot: Boolean,
    ) : ShotLogListUiState
    data class Error(val message: String) : ShotLogListUiState
}
