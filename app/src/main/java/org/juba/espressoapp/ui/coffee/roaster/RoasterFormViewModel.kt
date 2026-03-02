package org.juba.espressoapp.ui.coffee.roaster

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.model.Roaster
import org.juba.espressoapp.domain.repository.RoasterRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RoasterFormViewModel @Inject constructor(
    private val repository: RoasterRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val roasterId: String? = savedStateHandle["roasterId"]

    private val _uiState = MutableStateFlow(RoasterFormUiState())
    val uiState: StateFlow<RoasterFormUiState> = _uiState.asStateFlow()

    init {
        roasterId?.let { loadRoaster(it) }
    }

    private fun loadRoaster(id: String) {
        viewModelScope.launch {
            repository.getById(id)?.let { roaster ->
                _uiState.update {
                    it.copy(
                        name = roaster.name,
                        country = roaster.country ?: "",
                        website = roaster.website ?: "",
                        notes = roaster.notes ?: "",
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update {
        it.copy(name = value, nameError = null)
    }

    fun onCountryChange(value: String) = _uiState.update { it.copy(country = value) }

    fun onWebsiteChange(value: String) = _uiState.update { it.copy(website = value) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Name is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()
            val roaster = Roaster(
                id = roasterId ?: UUID.randomUUID().toString(),
                name = state.name.trim(),
                country = state.country.trim().ifBlank { null },
                website = state.website.trim().ifBlank { null },
                imageUri = null,
                notes = state.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now,
            )
            val result = if (roasterId == null) repository.insert(roaster)
            else repository.update(roaster)

            _uiState.update {
                if (result.isSuccess) it.copy(isSaving = false, isSaved = true)
                else it.copy(isSaving = false)
            }
        }
    }
}
