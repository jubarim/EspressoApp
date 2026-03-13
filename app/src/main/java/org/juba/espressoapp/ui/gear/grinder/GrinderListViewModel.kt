package org.juba.espressoapp.ui.gear.grinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.repository.GrinderRepository
import javax.inject.Inject

@HiltViewModel
class GrinderListViewModel @Inject constructor(
    repository: GrinderRepository,
) : ViewModel() {

    val uiState: StateFlow<GrinderListUiState> = repository
        .getAll()
        .map<_, GrinderListUiState> { GrinderListUiState.Success(it) }
        .catch { emit(GrinderListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GrinderListUiState.Loading,
        )
}
