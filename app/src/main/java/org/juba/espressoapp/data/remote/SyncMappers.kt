package org.juba.espressoapp.data.remote

import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.EspressoMachineEntity
import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.GrinderEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.data.local.entity.ShotLogEntity

/**
 * Version of the remote document shape. Bump when a field is renamed or its meaning changes,
 * so the merge can detect documents written by an older app version.
 */
internal const val REMOTE_DOCUMENT_VERSION = 1L

// Integer columns are written as Long so every number read back is a Long, which keeps the
// pull side free of Int/Long branching.

internal fun RoasterEntity.toRemoteDocument(): RemoteDocument = RemoteDocument(
    collection = SyncCollection.ROASTERS,
    id = id,
    fields = mapOf(
        "id" to id,
        "name" to name,
        "country" to country,
        "city" to city,
        "website" to website,
        "imageUri" to imageUri,
        "notes" to notes,
        "isDeleted" to isDeleted,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "docVersion" to REMOTE_DOCUMENT_VERSION,
    ),
)

internal fun CoffeeBeanEntity.toRemoteDocument(): RemoteDocument = RemoteDocument(
    collection = SyncCollection.COFFEE_BEANS,
    id = id,
    fields = mapOf(
        "id" to id,
        "roasterId" to roasterId,
        "name" to name,
        "origin" to origin,
        "process" to process,
        "roastLevel" to roastLevel,
        "roastDate" to roastDate,
        "imageUri" to imageUri,
        "notes" to notes,
        "isDeleted" to isDeleted,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "docVersion" to REMOTE_DOCUMENT_VERSION,
    ),
)

internal fun GrinderEntity.toRemoteDocument(): RemoteDocument = RemoteDocument(
    collection = SyncCollection.GRINDERS,
    id = id,
    fields = mapOf(
        "id" to id,
        "brand" to brand,
        "model" to model,
        "burrType" to burrType,
        "burrSize" to burrSize,
        "purchaseDate" to purchaseDate,
        "burrInstallDate" to burrInstallDate,
        "imageUri" to imageUri,
        "notes" to notes,
        "isDeleted" to isDeleted,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "docVersion" to REMOTE_DOCUMENT_VERSION,
    ),
)

internal fun EspressoMachineEntity.toRemoteDocument(): RemoteDocument = RemoteDocument(
    collection = SyncCollection.ESPRESSO_MACHINES,
    id = id,
    fields = mapOf(
        "id" to id,
        "brand" to brand,
        "model" to model,
        "boilerType" to boilerType,
        "pumpType" to pumpType,
        "groupHead" to groupHead,
        "hasPressureGauge" to hasPressureGauge,
        "purchaseDate" to purchaseDate,
        "imageUri" to imageUri,
        "notes" to notes,
        "isDeleted" to isDeleted,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "docVersion" to REMOTE_DOCUMENT_VERSION,
    ),
)

internal fun FilterBasketEntity.toRemoteDocument(): RemoteDocument = RemoteDocument(
    collection = SyncCollection.FILTER_BASKETS,
    id = id,
    fields = mapOf(
        "id" to id,
        "brand" to brand,
        "model" to model,
        "sizeGrams" to sizeGrams,
        "sizeClass" to sizeClass,
        "type" to type,
        "diameter" to diameter,
        "purchaseDate" to purchaseDate,
        "imageUri" to imageUri,
        "notes" to notes,
        "isDeleted" to isDeleted,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "docVersion" to REMOTE_DOCUMENT_VERSION,
    ),
)

internal fun ShotLogEntity.toRemoteDocument(): RemoteDocument = RemoteDocument(
    collection = SyncCollection.SHOT_LOGS,
    id = id,
    fields = mapOf(
        "id" to id,
        "coffeeBeanId" to coffeeBeanId,
        "grinderId" to grinderId,
        "machineId" to machineId,
        "basketId" to basketId,
        "grindSetting" to grindSetting,
        "doseGrams" to doseGrams,
        "yieldGrams" to yieldGrams,
        "extractionTimeSeconds" to extractionTimeSeconds?.toLong(),
        "brewTemperatureCelsius" to brewTemperatureCelsius,
        "preInfusionSeconds" to preInfusionSeconds?.toLong(),
        "rating" to rating?.toLong(),
        "notes" to notes,
        "shotAt" to shotAt,
        "isDeleted" to isDeleted,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "docVersion" to REMOTE_DOCUMENT_VERSION,
    ),
)
