package org.juba.espressoapp.ui.gear.grinder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.model.Grinder
import org.juba.espressoapp.domain.repository.GrinderRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GrinderFormViewModel @Inject constructor(
    private val repository: GrinderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val grinderId: String? = savedStateHandle[GRINDER_ID]

    private val _uiState = MutableStateFlow(GrinderFormUiState())
    val uiState: StateFlow<GrinderFormUiState> = _uiState.asStateFlow()

    init {
        grinderId?.let { loadGrinder(it) }
    }

    private fun loadGrinder(id: String) {
        viewModelScope.launch {
            repository.getById(id)?.let { grinder ->
                _uiState.update {
                    it.copy(
                        brand = grinder.brand,
                        model = grinder.model,
                        burrType = grinder.burrType ?: "",
                        burrSize = grinder.burrSize ?: "",
                        purchaseDate = grinder.purchaseDate,
                        burrInstallDate = grinder.burrInstallDate,
                        imageUrl = grinder.imageUri ?: "",
                        notes = grinder.notes ?: "",
                    )
                }
            }
        }
    }

    fun onBrandChange(value: String) = _uiState.update { it.copy(brand = value, brandError = null) }

    fun onModelChange(value: String) = _uiState.update { it.copy(model = value, modelError = null) }

    fun onBurrTypeChange(value: String) = _uiState.update { it.copy(burrType = value) }

    fun onBurrSizeChange(value: String) = _uiState.update { it.copy(burrSize = value) }

    fun onPurchaseDateChange(value: Long?) = _uiState.update { it.copy(purchaseDate = value) }

    fun onBurrInstallDateChange(value: Long?) = _uiState.update { it.copy(burrInstallDate = value) }

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
            val grinder = Grinder(
                id = grinderId ?: UUID.randomUUID().toString(),
                brand = state.brand.trim(),
                model = state.model.trim(),
                burrType = state.burrType.ifBlank { null },
                burrSize = state.burrSize.trim().ifBlank { null },
                purchaseDate = state.purchaseDate,
                burrInstallDate = state.burrInstallDate,
                imageUri = state.imageUrl.trim().ifBlank { null },
                notes = state.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now,
            )
            val result = if (grinderId == null) repository.insert(grinder)
            else repository.update(grinder)

            _uiState.update {
                if (result.isSuccess) it.copy(isSaving = false, isSaved = true)
                else it.copy(isSaving = false)
            }
        }
    }

    companion object {
        const val GRINDER_ID = "grinderId"
    }
}
