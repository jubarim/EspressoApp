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
import org.juba.espressoapp.domain.repository.RoasterRepository
import javax.inject.Inject

@HiltViewModel
class RoasterDetailViewModel @Inject constructor(
    private val repository: RoasterRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val roasterId: String = checkNotNull(savedStateHandle["roasterId"])

    private val _uiState = MutableStateFlow<RoasterDetailUiState>(RoasterDetailUiState.Loading)
    val uiState: StateFlow<RoasterDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val roaster = repository.getById(roasterId)
            _uiState.update {
                if (roaster != null) RoasterDetailUiState.Success(roaster)
                else RoasterDetailUiState.Error("Roaster not found")
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(roasterId)
            _uiState.update { RoasterDetailUiState.Deleted }
        }
    }
}
