package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.EspressoMachineEntity
import org.juba.espressoapp.domain.model.EspressoMachine

fun EspressoMachineEntity.toDomain(): EspressoMachine = EspressoMachine(
    id = id,
    brand = brand,
    model = model,
    boilerType = boilerType,
    pumpType = pumpType,
    groupHead = groupHead,
    hasPressureGauge = hasPressureGauge,
    purchaseDate = purchaseDate,
    imageUri = imageUri,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun EspressoMachine.toEntity(): EspressoMachineEntity = EspressoMachineEntity(
    id = id,
    brand = brand,
    model = model,
    boilerType = boilerType,
    pumpType = pumpType,
    groupHead = groupHead,
    hasPressureGauge = hasPressureGauge,
    purchaseDate = purchaseDate,
    imageUri = imageUri,
    notes = notes,
    isDeleted = false,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
