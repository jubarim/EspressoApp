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
import org.juba.espressoapp.domain.model.EspressoMachine
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EspressoMachineFormViewModel @Inject constructor(
    private val repository: EspressoMachineRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val machineId: String? = savedStateHandle[MACHINE_ID]

    private val _uiState = MutableStateFlow(EspressoMachineFormUiState())
    val uiState: StateFlow<EspressoMachineFormUiState> = _uiState.asStateFlow()

    init {
        machineId?.let { loadMachine(it) }
    }

    private fun loadMachine(id: String) {
        viewModelScope.launch {
            repository.getById(id)?.let { machine ->
                _uiState.update {
                    it.copy(
                        brand = machine.brand,
                        model = machine.model,
                        boilerType = machine.boilerType ?: "",
                        pumpType = machine.pumpType ?: "",
                        groupHead = machine.groupHead ?: "",
                        hasPressureGauge = machine.hasPressureGauge,
                        purchaseDate = machine.purchaseDate,
                        imageUrl = machine.imageUri ?: "",
                        notes = machine.notes ?: "",
                    )
                }
            }
        }
    }

    fun onBrandChange(value: String) = _uiState.update { it.copy(brand = value, brandError = null) }

    fun onModelChange(value: String) = _uiState.update { it.copy(model = value, modelError = null) }

    fun onBoilerTypeChange(value: String) = _uiState.update { it.copy(boilerType = value) }

    fun onPumpTypeChange(value: String) = _uiState.update { it.copy(pumpType = value) }

    fun onGroupHeadChange(value: String) = _uiState.update { it.copy(groupHead = value) }

    fun onHasPressureGaugeChange(value: Boolean) = _uiState.update { it.copy(hasPressureGauge = value) }

    fun onPurchaseDateChange(value: Long?) = _uiState.update { it.copy(purchaseDate = value) }

    fun onImageUrlChange(value: String) = _uiState.update { it.copy(imageUrl = value) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        val brandIsBlank = state.brand.isBlank()
        val modelIsBlank = state.model.isBlank()
        if (brandIsBlank || modelIsBlank) {
            _uiState.update {
                it.copy(
                    brandError = if (brandIsBlank) "Brand is required" else null,
                    modelError = if (modelIsBlank) "Model is required" else null,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()
            val machine = EspressoMachine(
                id = machineId ?: UUID.randomUUID().toString(),
                brand = state.brand.trim(),
                model = state.model.trim(),
                boilerType = state.boilerType.ifBlank { null },
                pumpType = state.pumpType.ifBlank { null },
                groupHead = state.groupHead.ifBlank { null },
                hasPressureGauge = state.hasPressureGauge,
                purchaseDate = state.purchaseDate,
                imageUri = state.imageUrl.trim().ifBlank { null },
                notes = state.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now,
            )
            val result = if (machineId == null) repository.insert(machine)
            else repository.update(machine)

            _uiState.update {
                if (result.isSuccess) it.copy(isSaving = false, isSaved = true)
                else it.copy(isSaving = false)
            }
        }
    }

    companion object {
        const val MACHINE_ID = "machineId"
    }
}
