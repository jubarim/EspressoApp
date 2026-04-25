package org.juba.espressoapp.data.backup

import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.EspressoMachineEntity
import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.GrinderEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.data.local.entity.ShotLogEntity

internal fun RoasterEntity.toDto() = RoasterDto(
    id = id, name = name, country = country, city = city,
    website = website, imageUri = imageUri, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt,
)

internal fun RoasterDto.toEntity() = RoasterEntity(
    id = id, name = name, country = country, city = city,
    website = website, imageUri = imageUri, notes = notes,
    isDeleted = false, createdAt = createdAt, updatedAt = updatedAt,
)

internal fun CoffeeBeanEntity.toDto() = CoffeeBeanDto(
    id = id, roasterId = roasterId, name = name, origin = origin,
    process = process, roastLevel = roastLevel, roastDate = roastDate,
    imageUri = imageUri, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt,
)

internal fun CoffeeBeanDto.toEntity() = CoffeeBeanEntity(
    id = id, roasterId = roasterId, name = name, origin = origin,
    process = process, roastLevel = roastLevel, roastDate = roastDate,
    imageUri = imageUri, notes = notes,
    isDeleted = false, createdAt = createdAt, updatedAt = updatedAt,
)

internal fun GrinderEntity.toDto() = GrinderDto(
    id = id, brand = brand, model = model, burrType = burrType,
    burrSize = burrSize, purchaseDate = purchaseDate,
    burrInstallDate = burrInstallDate, imageUri = imageUri, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt,
)

internal fun GrinderDto.toEntity() = GrinderEntity(
    id = id, brand = brand, model = model, burrType = burrType,
    burrSize = burrSize, purchaseDate = purchaseDate,
    burrInstallDate = burrInstallDate, imageUri = imageUri, notes = notes,
    isDeleted = false, createdAt = createdAt, updatedAt = updatedAt,
)

internal fun EspressoMachineEntity.toDto() = EspressoMachineDto(
    id = id, brand = brand, model = model, boilerType = boilerType,
    pumpType = pumpType, groupHead = groupHead,
    hasPressureGauge = hasPressureGauge, purchaseDate = purchaseDate,
    imageUri = imageUri, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt,
)

internal fun EspressoMachineDto.toEntity() = EspressoMachineEntity(
    id = id, brand = brand, model = model, boilerType = boilerType,
    pumpType = pumpType, groupHead = groupHead,
    hasPressureGauge = hasPressureGauge, purchaseDate = purchaseDate,
    imageUri = imageUri, notes = notes,
    isDeleted = false, createdAt = createdAt, updatedAt = updatedAt,
)

internal fun FilterBasketEntity.toDto() = FilterBasketDto(
    id = id, brand = brand, model = model, sizeGrams = sizeGrams,
    type = type, diameter = diameter, purchaseDate = purchaseDate,
    imageUri = imageUri, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt,
)

internal fun FilterBasketDto.toEntity() = FilterBasketEntity(
    id = id, brand = brand, model = model, sizeGrams = sizeGrams,
    type = type, diameter = diameter, purchaseDate = purchaseDate,
    imageUri = imageUri, notes = notes,
    isDeleted = false, createdAt = createdAt, updatedAt = updatedAt,
)

internal fun ShotLogEntity.toDto() = ShotLogDto(
    id = id, coffeeBeanId = coffeeBeanId, grinderId = grinderId,
    machineId = machineId, basketId = basketId,
    grindSetting = grindSetting, doseGrams = doseGrams,
    yieldGrams = yieldGrams, extractionTimeSeconds = extractionTimeSeconds,
    brewTemperatureCelsius = brewTemperatureCelsius,
    preInfusionSeconds = preInfusionSeconds, rating = rating,
    notes = notes, shotAt = shotAt,
    createdAt = createdAt, updatedAt = updatedAt,
)

internal fun ShotLogDto.toEntity() = ShotLogEntity(
    id = id, coffeeBeanId = coffeeBeanId, grinderId = grinderId,
    machineId = machineId, basketId = basketId,
    grindSetting = grindSetting, doseGrams = doseGrams,
    yieldGrams = yieldGrams, extractionTimeSeconds = extractionTimeSeconds,
    brewTemperatureCelsius = brewTemperatureCelsius,
    preInfusionSeconds = preInfusionSeconds, rating = rating,
    notes = notes, shotAt = shotAt,
    isDeleted = false, createdAt = createdAt, updatedAt = updatedAt,
)
