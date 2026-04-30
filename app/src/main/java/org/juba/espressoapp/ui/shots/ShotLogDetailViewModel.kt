package org.juba.espressoapp.ui.shots

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.repository.ShotLogRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShotLogDetailViewModel @Inject constructor(
    private val repository: ShotLogRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val shotId: String = checkNotNull(savedStateHandle[ShotLogFormViewModel.SHOT_ID])

    private val _uiState = MutableStateFlow<ShotLogDetailUiState>(ShotLogDetailUiState.Loading)
    val uiState: StateFlow<ShotLogDetailUiState> = _uiState.asStateFlow()

    private val _copyEvent = Channel<String>(Channel.BUFFERED)

    /** Emits the new shot's ID after a successful [copyShot] call. */
    val copyEvent: Flow<String> = _copyEvent.receiveAsFlow()

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

    /**
     * Creates a copy of the current shot with the given [newShotAt] timestamp, then emits the new
     * shot's ID via [copyEvent] on success.
     */
    fun copyShot(newShotAt: Long) {
        val shot = (uiState.value as? ShotLogDetailUiState.Success)?.shot ?: return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val newShot = shot.copy(
                id = UUID.randomUUID().toString(),
                shotAt = newShotAt,
                createdAt = now,
                updatedAt = now,
            )
            repository.insert(newShot)
                .onSuccess { _copyEvent.send(newShot.id) }
        }
    }
}
