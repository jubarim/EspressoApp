package org.juba.espressoapp.ui.shots

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.repository.ShotLogRepository
import javax.inject.Inject

@HiltViewModel
class ShotLogDetailViewModel @Inject constructor(
    private val repository: ShotLogRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val shotId: String = checkNotNull(savedStateHandle[ShotLogFormViewModel.SHOT_ID])

    private val _uiState = MutableStateFlow<ShotLogDetailUiState>(ShotLogDetailUiState.Loading)
    val uiState: StateFlow<ShotLogDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val shot = repository.getById(shotId)
            _uiState.update {
                if (shot != null) ShotLogDetailUiState.Success(shot)
                else ShotLogDetailUiState.Error("Shot not found")
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(shotId)
            _uiState.update { ShotLogDetailUiState.Deleted }
        }
    }
}
