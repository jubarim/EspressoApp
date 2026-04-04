package org.juba.espressoapp.ui.shots

import org.juba.espressoapp.designsystem.SelectionOption

data class ShotLogFormUiState(
    // Equipment selections
    val coffeeBeanId: String = "",
    val coffeeBeanLabel: String? = null,
    val grinderId: String = "",
    val grinderLabel: String? = null,
    val machineId: String = "",
    val machineLabel: String? = null,
    val basketId: String = "",
    val basketLabel: String? = null,
    // Equipment options for pickers (populated from repositories)
    val coffeeBeanOptions: List<SelectionOption> = emptyList(),
    val grinderOptions: List<SelectionOption> = emptyList(),
    val machineOptions: List<SelectionOption> = emptyList(),
    val basketOptions: List<SelectionOption> = emptyList(),
    // Shot measurement fields
    val dose: String = "",
    val yield: String = "",
    val grindSetting: String = "",
    val extractionTimeSeconds: String = "",
    val brewTemperatureCelsius: String = "",
    val preInfusionSeconds: String = "",
    val rating: Int? = null,
    val notes: String = "",
    val shotAt: Long = System.currentTimeMillis(),
    // Validation errors
    val coffeeBeanError: String? = null,
    val grinderError: String? = null,
    val machineError: String? = null,
    val basketError: String? = null,
    val doseError: String? = null,
    val yieldError: String? = null,
    // Save state
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
)
