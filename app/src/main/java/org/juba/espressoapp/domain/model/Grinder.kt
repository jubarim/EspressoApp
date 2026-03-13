package org.juba.espressoapp.domain.model

data class Grinder(
    val id: String,
    val brand: String,
    val model: String,
    val burrType: String?,
    val burrSize: String?,
    val purchaseDate: Long?,
    val burrInstallDate: Long?,
    val imageUri: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
)