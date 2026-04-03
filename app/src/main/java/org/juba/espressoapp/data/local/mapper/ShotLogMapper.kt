package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.ShotLogEntity
import org.juba.espressoapp.data.local.entity.ShotLogWithDetails
import org.juba.espressoapp.domain.model.ShotLog

fun ShotLogWithDetails.toDomain(): ShotLog = ShotLog(
    id = shot.id,
    coffeeBeanId = shot.coffeeBeanId,
    coffeeBeanName = coffeeBeanName,
    roasterName = roasterName,
    grinderId = shot.grinderId,
    grinderName = grinderName,
    machineId = shot.machineId,
    machineName = machineName,
    basketId = shot.basketId,
    basketName = basketName,
    grindSetting = shot.grindSetting,
    doseGrams = shot.doseGrams,
    yieldGrams = shot.yieldGrams,
    ratio = if (shot.doseGrams > 0) shot.yieldGrams / shot.doseGrams else 0.0,
    extractionTimeSeconds = shot.extractionTimeSeconds,
    brewTemperatureCelsius = shot.brewTemperatureCelsius,
    preInfusionSeconds = shot.preInfusionSeconds,
    rating = shot.rating,
    notes = shot.notes,
    shotAt = shot.shotAt,
    createdAt = shot.createdAt,
    updatedAt = shot.updatedAt,
)

fun ShotLog.toEntity(): ShotLogEntity = ShotLogEntity(
    id = id,
    coffeeBeanId = coffeeBeanId,
    grinderId = grinderId,
    machineId = machineId,
    basketId = basketId,
    grindSetting = grindSetting,
    doseGrams = doseGrams,
    yieldGrams = yieldGrams,
    extractionTimeSeconds = extractionTimeSeconds,
    brewTemperatureCelsius = brewTemperatureCelsius,
    preInfusionSeconds = preInfusionSeconds,
    rating = rating,
    notes = notes,
    shotAt = shotAt,
    isDeleted = false,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
