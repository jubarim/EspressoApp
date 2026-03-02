package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.domain.model.Roaster

fun RoasterEntity.toDomain(): Roaster = Roaster(
    id = id,
    name = name,
    country = country,
    website = website,
    imageUri = imageUri,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Roaster.toEntity(): RoasterEntity = RoasterEntity(
    id = id,
    name = name,
    country = country,
    website = website,
    imageUri = imageUri,
    notes = notes,
    isDeleted = false,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
