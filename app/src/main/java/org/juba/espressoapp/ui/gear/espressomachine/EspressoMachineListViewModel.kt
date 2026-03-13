package org.juba.espressoapp.ui.gear.espressomachine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import javax.inject.Inject

@HiltViewModel
class EspressoMachineListViewModel @Inject constructor(
    repository: EspressoMachineRepository,
) : ViewModel() {

    val uiState: StateFlow<EspressoMachineListUiState> = repository
        .getAll()
        .map<_, EspressoMachineListUiState> { EspressoMachineListUiState.Success(it) }
        .catch { emit(EspressoMachineListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EspressoMachineListUiState.Loading,
        )
}
