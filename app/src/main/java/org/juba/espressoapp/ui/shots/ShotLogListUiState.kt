package org.juba.espressoapp.ui.shots

import org.juba.espressoapp.domain.model.ShotLog
import org.juba.espressoapp.ui.filter.ShotFilter

sealed interface ShotLogListUiState {
    data object Loading : ShotLogListUiState
    data class Success(
        val shots: List<ShotLog>,
        val canAddShot: Boolean,
        val activeFilter: ShotFilter? = null,
        val totalShotCount: Int = shots.size,
    ) : ShotLogListUiState
    data class Error(val message: String) : ShotLogListUiState
}
