package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.CoffeeBeanWithRoaster
import org.juba.espressoapp.domain.model.CoffeeBean

fun CoffeeBeanWithRoaster.toDomain(): CoffeeBean = CoffeeBean(
    id = bean.id,
    roasterId = bean.roasterId,
    roasterName = roasterName,
    name = bean.name,
    origin = bean.origin,
    process = bean.process,
    roastLevel = bean.roastLevel,
    roastDate = bean.roastDate,
    imageUri = bean.imageUri,
    notes = bean.notes,
    createdAt = bean.createdAt,
    updatedAt = bean.updatedAt,
)

fun CoffeeBean.toEntity(): CoffeeBeanEntity = CoffeeBeanEntity(
    id = id,
    roasterId = roasterId,
    name = name,
    origin = origin,
    process = process,
    roastLevel = roastLevel,
    roastDate = roastDate,
    imageUri = imageUri,
    notes = notes,
    isDeleted = false,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
