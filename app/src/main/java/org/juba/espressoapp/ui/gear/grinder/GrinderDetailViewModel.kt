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
import org.juba.espressoapp.domain.repository.GrinderRepository
import javax.inject.Inject

@HiltViewModel
class GrinderDetailViewModel @Inject constructor(
    private val repository: GrinderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val grinderId: String = checkNotNull(savedStateHandle[GrinderFormViewModel.GRINDER_ID])

    private val _uiState = MutableStateFlow<GrinderDetailUiState>(GrinderDetailUiState.Loading)
    val uiState: StateFlow<GrinderDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val grinder = repository.getById(grinderId)
            _uiState.update {
                if (grinder != null) GrinderDetailUiState.Success(grinder)
                else GrinderDetailUiState.Error("Grinder not found")
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(grinderId)
            _uiState.update { GrinderDetailUiState.Deleted }
        }
    }
}
