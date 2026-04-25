package org.juba.espressoapp.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupDto(
    val schemaVersion: Int = 7,
    val exportedAt: Long,
    val roasters: List<RoasterDto>,
    val coffeeBeans: List<CoffeeBeanDto>,
    val grinders: List<GrinderDto>,
    val espressoMachines: List<EspressoMachineDto>,
    val filterBaskets: List<FilterBasketDto>,
    val shotLogs: List<ShotLogDto>,
)

@Serializable
data class RoasterDto(
    val id: String,
    val name: String,
    val country: String? = null,
    val city: String? = null,
    val website: String? = null,
    val imageUri: String? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class CoffeeBeanDto(
    val id: String,
    val roasterId: String,
    val name: String,
    val origin: String? = null,
    val process: String? = null,
    val roastLevel: String? = null,
    val roastDate: Long? = null,
    val imageUri: String? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class GrinderDto(
    val id: String,
    val brand: String,
    val model: String,
    val burrType: String? = null,
    val burrSize: String? = null,
    val purchaseDate: Long? = null,
    val burrInstallDate: Long? = null,
    val imageUri: String? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class EspressoMachineDto(
    val id: String,
    val brand: String,
    val model: String,
    val boilerType: String? = null,
    val pumpType: String? = null,
    val groupHead: String? = null,
    val hasPressureGauge: Boolean = false,
    val purchaseDate: Long? = null,
    val imageUri: String? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class FilterBasketDto(
    val id: String,
    val brand: String,
    val model: String? = null,
    val sizeGrams: String? = null,
    val type: String? = null,
    val diameter: String? = null,
    val purchaseDate: Long? = null,
    val imageUri: String? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
data class ShotLogDto(
    val id: String,
    val coffeeBeanId: String,
    val grinderId: String,
    val machineId: String,
    val basketId: String,
    val grindSetting: String? = null,
    val doseGrams: Double,
    val yieldGrams: Double,
    val extractionTimeSeconds: Int? = null,
    val brewTemperatureCelsius: Double? = null,
    val preInfusionSeconds: Int? = null,
    val rating: Int? = null,
    val notes: String? = null,
    val shotAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
