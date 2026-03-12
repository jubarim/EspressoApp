package org.juba.espressoapp.domain.model

data class CoffeeBean(
    val id: String,
    val roasterId: String,
    val roasterName: String?,
    val name: String,
    val origin: String?,
    val process: String?,
    val roastLevel: String?,
    val roastDate: Long?,
    val imageUri: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
