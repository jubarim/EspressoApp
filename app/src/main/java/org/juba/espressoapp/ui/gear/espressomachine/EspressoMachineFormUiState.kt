package org.juba.espressoapp.ui.gear.espressomachine

data class EspressoMachineFormUiState(
    val brand: String = "",
    val model: String = "",
    val boilerType: String = "",
    val pumpType: String = "",
    val groupHead: String = "",
    val hasPressureGauge: Boolean = false,
    val purchaseDate: Long? = null,
    val imageUrl: String = "",
    val notes: String = "",
    val brandError: String? = null,
    val modelError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
)
