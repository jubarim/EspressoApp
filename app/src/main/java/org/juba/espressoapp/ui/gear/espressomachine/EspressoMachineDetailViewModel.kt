package org.juba.espressoapp.ui.gear.espressomachine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import javax.inject.Inject

@HiltViewModel
class EspressoMachineDetailViewModel @Inject constructor(
    private val repository: EspressoMachineRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val machineId: String = checkNotNull(savedStateHandle[EspressoMachineFormViewModel.MACHINE_ID])

    private val _uiState = MutableStateFlow<EspressoMachineDetailUiState>(EspressoMachineDetailUiState.Loading)
    val uiState: StateFlow<EspressoMachineDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val machine = repository.getById(machineId)
            _uiState.update {
                if (machine != null) EspressoMachineDetailUiState.Success(machine)
                else EspressoMachineDetailUiState.Error("Espresso machine not found")
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(machineId)
            _uiState.update { EspressoMachineDetailUiState.Deleted }
        }
    }
}
