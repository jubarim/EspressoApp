package org.juba.espressoapp.ui.shots

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.designsystem.SelectionOption
import org.juba.espressoapp.domain.model.ShotLog
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import org.juba.espressoapp.domain.repository.GrinderRepository
import org.juba.espressoapp.domain.repository.ShotLogRepository
import org.juba.espressoapp.extensions.normalizeDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShotLogFormViewModel @Inject constructor(
    private val shotLogRepository: ShotLogRepository,
    coffeeBeanRepository: CoffeeBeanRepository,
    grinderRepository: GrinderRepository,
    espressoMachineRepository: EspressoMachineRepository,
    filterBasketRepository: FilterBasketRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val shotId: String? = savedStateHandle[SHOT_ID]

    private val _uiState = MutableStateFlow(ShotLogFormUiState())
    val uiState: StateFlow<ShotLogFormUiState> = _uiState.asStateFlow()

    init {
        coffeeBeanRepository.getAll()
            .onEach { beans ->
                _uiState.update { state ->
                    state.copy(
                        coffeeBeanOptions = beans.map { bean ->
                            SelectionOption(
                                key = bean.id,
                                label = if (bean.roasterName != null) "${bean.name} · ${bean.roasterName}" else bean.name,
                            )
                        },
                    )
                }
            }
            .launchIn(viewModelScope)

        grinderRepository.getAll()
            .onEach { grinders ->
                _uiState.update { state ->
                    state.copy(
                        grinderOptions = grinders.map { grinder ->
                            SelectionOption(key = grinder.id, label = "${grinder.brand} ${grinder.model}")
                        },
                    )
                }
            }
            .launchIn(viewModelScope)

        espressoMachineRepository.getAll()
            .onEach { machines ->
                _uiState.update { state ->
                    state.copy(
                        machineOptions = machines.map { machine ->
                            SelectionOption(key = machine.id, label = "${machine.brand} ${machine.model}")
                        },
                    )
                }
            }
            .launchIn(viewModelScope)

        filterBasketRepository.getAll()
            .onEach { baskets ->
                _uiState.update { state ->
                    state.copy(
                        basketOptions = baskets.map { basket ->
                            SelectionOption(
                                key = basket.id,
                                label = if (basket.model != null) "${basket.brand} ${basket.model}" else basket.brand,
                            )
                        },
                    )
                }
            }
            .launchIn(viewModelScope)

        shotId?.let { loadShot(it) }
    }

    private fun loadShot(id: String) {
        viewModelScope.launch {
            shotLogRepository.getById(id)?.let { shot ->
                _uiState.update {
                    it.copy(
                        coffeeBeanId = shot.coffeeBeanId,
                        coffeeBeanLabel = shot.coffeeBeanName,
                        grinderId = shot.grinderId,
                        grinderLabel = shot.grinderName,
                        machineId = shot.machineId,
                        machineLabel = shot.machineName,
                        basketId = shot.basketId,
                        basketLabel = shot.basketName,
                        dose = "%.1f".format(shot.doseGrams),
                        yield = "%.1f".format(shot.yieldGrams),
                        grindSetting = shot.grindSetting ?: "",
                        extractionTimeSeconds = shot.extractionTimeSeconds?.toString() ?: "",
                        brewTemperatureCelsius = shot.brewTemperatureCelsius?.let { t -> "%.1f".format(t) } ?: "",
                        preInfusionSeconds = shot.preInfusionSeconds?.toString() ?: "",
                        rating = shot.rating,
                        notes = shot.notes ?: "",
                        shotAt = shot.shotAt,
                    )
                }
            }
        }
    }

    fun onCoffeeBeanChange(id: String, label: String) {
        _uiState.update { it.copy(coffeeBeanId = id, coffeeBeanLabel = label, coffeeBeanError = null) }
    }

    fun onGrinderChange(id: String, label: String) {
        _uiState.update { it.copy(grinderId = id, grinderLabel = label, grinderError = null) }
    }

    fun onMachineChange(id: String, label: String) {
        _uiState.update { it.copy(machineId = id, machineLabel = label, machineError = null) }
    }

    fun onBasketChange(id: String, label: String) {
        _uiState.update { it.copy(basketId = id, basketLabel = label, basketError = null) }
    }

    fun onDoseChange(value: String) = _uiState.update { it.copy(dose = value, doseError = null) }

    fun onYieldChange(value: String) = _uiState.update { it.copy(yield = value, yieldError = null) }

    fun onGrindSettingChange(value: String) = _uiState.update { it.copy(grindSetting = value) }

    fun onExtractionTimeChange(value: String) = _uiState.update { it.copy(extractionTimeSeconds = value) }

    fun onBrewTemperatureChange(value: String) = _uiState.update { it.copy(brewTemperatureCelsius = value) }

    fun onPreInfusionChange(value: String) = _uiState.update { it.copy(preInfusionSeconds = value) }

    fun onRatingChange(value: Int?) = _uiState.update { it.copy(rating = value) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onShotAtChange(value: Long) = _uiState.update { it.copy(shotAt = value) }

    fun save() {
        val state = _uiState.value
        var hasError = false

        if (state.coffeeBeanId.isBlank()) {
            _uiState.update { it.copy(coffeeBeanError = "Coffee bean is required") }
            hasError = true
        }
        if (state.grinderId.isBlank()) {
            _uiState.update { it.copy(grinderError = "Grinder is required") }
            hasError = true
        }
        if (state.machineId.isBlank()) {
            _uiState.update { it.copy(machineError = "Espresso machine is required") }
            hasError = true
        }
        if (state.basketId.isBlank()) {
            _uiState.update { it.copy(basketError = "Filter basket is required") }
            hasError = true
        }

        val dose = state.dose.normalizeDecimal().toDoubleOrNull()
        if (dose == null || dose <= 0) {
            _uiState.update { it.copy(doseError = "Valid dose is required") }
            hasError = true
        }
        val yieldGrams = state.yield.normalizeDecimal().toDoubleOrNull()
        if (yieldGrams == null || yieldGrams <= 0) {
            _uiState.update { it.copy(yieldError = "Valid yield is required") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()
            val shot = ShotLog(
                id = shotId ?: UUID.randomUUID().toString(),
                coffeeBeanId = state.coffeeBeanId,
                coffeeBeanName = state.coffeeBeanLabel,
                roasterName = null,
                grinderId = state.grinderId,
                grinderName = state.grinderLabel,
                machineId = state.machineId,
                machineName = state.machineLabel,
                basketId = state.basketId,
                basketName = state.basketLabel,
                grindSetting = state.grindSetting.trim().ifBlank { null },
                doseGrams = dose!!,
                yieldGrams = yieldGrams!!,
                ratio = if (dose > 0) yieldGrams / dose else 0.0,
                extractionTimeSeconds = state.extractionTimeSeconds.trim().toIntOrNull(),
                brewTemperatureCelsius = state.brewTemperatureCelsius.normalizeDecimal().toDoubleOrNull(),
                preInfusionSeconds = state.preInfusionSeconds.trim().toIntOrNull(),
                rating = state.rating,
                notes = state.notes.trim().ifBlank { null },
                shotAt = state.shotAt,
                createdAt = now,
                updatedAt = now,
            )
            val result = if (shotId == null) shotLogRepository.insert(shot)
            else shotLogRepository.update(shot)
            _uiState.update {
                if (result.isSuccess) it.copy(isSaving = false, isSaved = true)
                else it.copy(isSaving = false)
            }
        }
    }

    companion object {
        const val SHOT_ID = "shotId"

        /** Rating options for the picker (key = Int string, label = display text). */
        val ratingOptions = listOf(
            SelectionOption("1", "1 – Poor"),
            SelectionOption("2", "2 – Fair"),
            SelectionOption("3", "3 – Good"),
            SelectionOption("4", "4 – Great"),
            SelectionOption("5", "5 – God Shot!"),
        )
    }
}

