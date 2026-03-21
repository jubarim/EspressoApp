package org.juba.espressoapp.ui.coffee.coffeebean

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
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CoffeeBeanDetailViewModel @Inject constructor(
    private val repository: CoffeeBeanRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val beanId: String = checkNotNull(savedStateHandle[CoffeeBeanFormViewModel.BEAN_ID])

    private val _uiState = MutableStateFlow<CoffeeBeanDetailUiState>(CoffeeBeanDetailUiState.Loading)
    val uiState: StateFlow<CoffeeBeanDetailUiState> = _uiState.asStateFlow()

    private val _copyEvent = Channel<String>(Channel.BUFFERED)

    /** Emits the new bean's ID after a successful [copyBean] call. */
    val copyEvent: Flow<String> = _copyEvent.receiveAsFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val bean = repository.getById(beanId)
            _uiState.update {
                if (bean != null) CoffeeBeanDetailUiState.Success(bean)
                else CoffeeBeanDetailUiState.Error("Coffee bean not found")
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(beanId)
            _uiState.update { CoffeeBeanDetailUiState.Deleted }
        }
    }

    /**
     * Creates a copy of the current bean with the given [newRoastDate], then emits the new bean's
     * ID via [copyEvent] on success.
     */
    fun copyBean(newRoastDate: Long?) {
        val bean = (uiState.value as? CoffeeBeanDetailUiState.Success)?.bean ?: return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val newBean = bean.copy(
                id = UUID.randomUUID().toString(),
                roastDate = newRoastDate,
                createdAt = now,
                updatedAt = now,
            )
            repository.insert(newBean)
                .onSuccess { _copyEvent.send(newBean.id) }
        }
    }
}
