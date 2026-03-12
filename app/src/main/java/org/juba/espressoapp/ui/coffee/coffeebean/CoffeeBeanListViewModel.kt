package org.juba.espressoapp.ui.coffee.coffeebean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import javax.inject.Inject

@HiltViewModel
class CoffeeBeanListViewModel @Inject constructor(
    repository: CoffeeBeanRepository,
) : ViewModel() {

    val uiState: StateFlow<CoffeeBeanListUiState> = repository
        .getAll()
        .map<_, CoffeeBeanListUiState> { CoffeeBeanListUiState.Success(it) }
        .catch { emit(CoffeeBeanListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoffeeBeanListUiState.Loading,
        )
}
