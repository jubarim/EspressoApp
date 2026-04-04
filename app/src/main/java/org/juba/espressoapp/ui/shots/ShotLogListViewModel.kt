package org.juba.espressoapp.ui.shots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import org.juba.espressoapp.domain.repository.GrinderRepository
import org.juba.espressoapp.domain.repository.ShotLogRepository
import javax.inject.Inject

@HiltViewModel
class ShotLogListViewModel @Inject constructor(
    shotLogRepository: ShotLogRepository,
    coffeeBeanRepository: CoffeeBeanRepository,
    grinderRepository: GrinderRepository,
    espressoMachineRepository: EspressoMachineRepository,
    filterBasketRepository: FilterBasketRepository,
) : ViewModel() {

    val uiState: StateFlow<ShotLogListUiState> = combine(
        shotLogRepository.getAll(),
        coffeeBeanRepository.getAll(),
        grinderRepository.getAll(),
        espressoMachineRepository.getAll(),
        filterBasketRepository.getAll(),
    ) { shots, beans, grinders, machines, baskets ->
        ShotLogListUiState.Success(
            shots = shots,
            canAddShot = beans.isNotEmpty() && grinders.isNotEmpty() &&
                machines.isNotEmpty() && baskets.isNotEmpty(),
        ) as ShotLogListUiState
    }
        .catch { emit(ShotLogListUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ShotLogListUiState.Loading,
        )
}
