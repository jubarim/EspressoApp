package org.juba.espressoapp.domain.model

data class ShotLog(
    val id: String,
    val coffeeBeanId: String,
    val coffeeBeanName: String?,
    val roasterName: String?,
    val grinderId: String,
    val grinderName: String?,
    val machineId: String,
    val machineName: String?,
    val basketId: String,
    val basketName: String?,
    val grindSetting: String?,
    val doseGrams: Double,
    val yieldGrams: Double,
    /** Computed at read time: [yieldGrams] / [doseGrams]. Returns 0.0 if dose is zero. */
    val ratio: Double,
    val extractionTimeSeconds: Int?,
    val brewTemperatureCelsius: Double?,
    val preInfusionSeconds: Int?,
    val rating: Int?,
    val notes: String?,
    val shotAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
