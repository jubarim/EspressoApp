package org.juba.espressoapp.ui.coffee.roaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.repository.RoasterRepository
import javax.inject.Inject

@HiltViewModel
class RoasterListViewModel @Inject constructor(
    repository: RoasterRepository,
) : ViewModel() {

    val uiState: StateFlow<RoasterListUiState> = repository
        .getAll()
        .map<_, RoasterListUiState> { RoasterListUiState.Success(it) }
        .catch { emit(RoasterListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RoasterListUiState.Loading,
        )
}
