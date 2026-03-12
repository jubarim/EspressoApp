package org.juba.espressoapp.ui.coffee.coffeebean

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import javax.inject.Inject

@HiltViewModel
class CoffeeBeanDetailViewModel @Inject constructor(
    private val repository: CoffeeBeanRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val beanId: String = checkNotNull(savedStateHandle[CoffeeBeanFormViewModel.BEAN_ID])

    private val _uiState = MutableStateFlow<CoffeeBeanDetailUiState>(CoffeeBeanDetailUiState.Loading)
    val uiState: StateFlow<CoffeeBeanDetailUiState> = _uiState.asStateFlow()

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
}
