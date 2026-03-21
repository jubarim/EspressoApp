package org.juba.espressoapp.ui.coffee.coffeebean

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import org.juba.espressoapp.domain.repository.RoasterRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CoffeeBeanFormViewModel @Inject constructor(
    private val coffeeBeanRepository: CoffeeBeanRepository,
    private val roasterRepository: RoasterRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val beanId: String? = savedStateHandle[BEAN_ID]
    private val sourceBeanId: String? = savedStateHandle[SOURCE_BEAN_ID]

    private val _uiState = MutableStateFlow(CoffeeBeanFormUiState())
    val uiState: StateFlow<CoffeeBeanFormUiState> = _uiState.asStateFlow()

    init {
        loadRoasters()
        beanId?.let { loadBean(it) }

        if (beanId == null) {
            sourceBeanId?.let { id ->
                viewModelScope.launch {
                    coffeeBeanRepository.getById(id)?.let { bean -> populateFromBean(bean) }
                }
            }
        }
    }

    private fun loadRoasters() {
        roasterRepository.getAll()
            .onEach { roasters -> _uiState.update { it.copy(roasters = roasters) } }
            .launchIn(viewModelScope)
    }

    private fun loadBean(id: String) {
        viewModelScope.launch {
            coffeeBeanRepository.getById(id)?.let { bean -> populateFromBean(bean) }
        }
    }

    private fun populateFromBean(bean: CoffeeBean) {
        _uiState.update {
            it.copy(
                name = bean.name,
                roasterId = bean.roasterId,
                roasterName = bean.roasterName ?: "",
                origin = bean.origin ?: "",
                process = bean.process ?: "",
                roastLevel = bean.roastLevel ?: "",
                roastDate = bean.roastDate,
                imageUrl = bean.imageUri ?: "",
                notes = bean.notes ?: "",
            )
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null) }

    fun onRoasterChange(roasterId: String, roasterName: String) = _uiState.update {
        it.copy(roasterId = roasterId, roasterName = roasterName, roasterError = null)
    }

    fun onOriginChange(value: String) = _uiState.update { it.copy(origin = value) }

    fun onProcessChange(value: String) = _uiState.update { it.copy(process = value) }

    fun onRoastLevelChange(value: String) = _uiState.update { it.copy(roastLevel = value) }

    fun onRoastDateChange(value: Long?) = _uiState.update { it.copy(roastDate = value) }

    fun onImageUrlChange(value: String) = _uiState.update { it.copy(imageUrl = value) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        val nameIsBlank = state.name.isBlank()
        val roasterIsBlank = state.roasterId.isBlank()
        if (nameIsBlank || roasterIsBlank) {
            _uiState.update {
                it.copy(
                    nameError = if (nameIsBlank) "Name is required" else null,
                    roasterError = if (roasterIsBlank) "Roaster is required" else null,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()
            val bean = CoffeeBean(
                id = beanId ?: UUID.randomUUID().toString(),
                roasterId = state.roasterId,
                roasterName = state.roasterName.ifBlank { null },
                name = state.name.trim(),
                origin = state.origin.trim().ifBlank { null },
                process = state.process.ifBlank { null },
                roastLevel = state.roastLevel.ifBlank { null },
                roastDate = state.roastDate,
                imageUri = state.imageUrl.trim().ifBlank { null },
                notes = state.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now,
            )
            val result = if (beanId == null) coffeeBeanRepository.insert(bean)
            else coffeeBeanRepository.update(bean)

            _uiState.update {
                if (result.isSuccess) it.copy(isSaving = false, isSaved = true)
                else it.copy(isSaving = false)
            }
        }
    }

    companion object {
        const val BEAN_ID = "beanId"
        const val SOURCE_BEAN_ID = "sourceBeanId"
    }
}
