package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.GrinderEntity
import org.juba.espressoapp.domain.model.Grinder

fun GrinderEntity.toDomain(): Grinder = Grinder(
    id = id,
    brand = brand,
    model = model,
    burrType = burrType,
    burrSize = burrSize,
    purchaseDate = purchaseDate,
    burrInstallDate = burrInstallDate,
    imageUri = imageUri,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Grinder.toEntity(): GrinderEntity = GrinderEntity(
    id = id,
    brand = brand,
    model = model,
    burrType = burrType,
    burrSize = burrSize,
    purchaseDate = purchaseDate,
    burrInstallDate = burrInstallDate,
    imageUri = imageUri,
    notes = notes,
    isDeleted = false,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
