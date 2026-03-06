package org.juba.espressoapp.domain.model

data class Roaster(
    val id: String,
    val name: String,
    val country: String?,
    val city: String?,
    val website: String?,
    val imageUri: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
