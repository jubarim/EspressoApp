package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.domain.model.FilterBasket

fun FilterBasketEntity.toDomain(): FilterBasket = FilterBasket(
    id = id,
    brand = brand,
    model = model,
    sizeGrams = sizeGrams,
    type = type,
    diameter = diameter,
    purchaseDate = purchaseDate,
    imageUri = imageUri,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun FilterBasket.toEntity(): FilterBasketEntity = FilterBasketEntity(
    id = id,
    brand = brand,
    model = model,
    sizeGrams = sizeGrams,
    type = type,
    diameter = diameter,
    purchaseDate = purchaseDate,
    imageUri = imageUri,
    notes = notes,
    isDeleted = false,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
