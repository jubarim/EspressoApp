package org.juba.espressoapp.ui.gear.espressomachine

import org.juba.espressoapp.domain.model.EspressoMachine

sealed interface EspressoMachineDetailUiState {
    data object Loading : EspressoMachineDetailUiState
    data class Success(val machine: EspressoMachine) : EspressoMachineDetailUiState
    data object Deleted : EspressoMachineDetailUiState
    data class Error(val message: String) : EspressoMachineDetailUiState
}
