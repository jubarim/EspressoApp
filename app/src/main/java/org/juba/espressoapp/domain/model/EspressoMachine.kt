package org.juba.espressoapp.domain.model

data class EspressoMachine(
    val id: String,
    val brand: String,
    val model: String,
    val boilerType: String?,
    val pumpType: String?,
    val groupHead: String?,
    val hasPressureGauge: Boolean,
    val purchaseDate: Long?,
    val imageUri: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
