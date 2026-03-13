package org.juba.espressoapp.ui.gear.espressomachine

import org.juba.espressoapp.domain.model.EspressoMachine

sealed interface EspressoMachineListUiState {
    data object Loading : EspressoMachineListUiState
    data class Success(val machines: List<EspressoMachine>) : EspressoMachineListUiState
    data class Error(val message: String) : EspressoMachineListUiState
}
