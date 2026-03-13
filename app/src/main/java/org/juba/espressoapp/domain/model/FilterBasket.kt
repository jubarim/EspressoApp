package org.juba.espressoapp.domain.model

data class FilterBasket(
    val id: String,
    val brand: String,
    val model: String?,
    val sizeGrams: String?,
    val type: String?,
    val diameter: String?,
    val purchaseDate: Long?,
    val imageUri: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
