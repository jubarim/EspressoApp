package org.juba.espressoapp.data.remote

import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.EspressoMachineEntity
import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.GrinderEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.data.local.entity.ShotLogEntity

// Inverse of SyncMappers. Each function returns null when a required field is missing or has
// the wrong type, so a malformed document is skipped instead of crashing the applier.

private fun Map<String, Any?>.string(key: String): String? = this[key] as? String

private fun Map<String, Any?>.long(key: String): Long? = (this[key] as? Number)?.toLong()

private fun Map<String, Any?>.double(key: String): Double? = (this[key] as? Number)?.toDouble()

private fun Map<String, Any?>.boolean(key: String): Boolean? = this[key] as? Boolean

private fun Map<String, Any?>.int(key: String): Int? = (this[key] as? Number)?.toInt()

internal fun RemoteDocument.toRoasterEntity(): RoasterEntity? {
    val name = fields.string("name") ?: return null
    val updatedAt = fields.long("updatedAt") ?: return null
    return RoasterEntity(
        id = id,
        name = name,
        country = fields.string("country"),
        city = fields.string("city"),
        website = fields.string("website"),
        imageUri = fields.string("imageUri"),
        notes = fields.string("notes"),
        isDeleted = fields.boolean("isDeleted") ?: false,
        createdAt = fields.long("createdAt") ?: updatedAt,
        updatedAt = updatedAt,
    )
}

internal fun RemoteDocument.toCoffeeBeanEntity(): CoffeeBeanEntity? {
    val roasterId = fields.string("roasterId") ?: return null
    val name = fields.string("name") ?: return null
    val updatedAt = fields.long("updatedAt") ?: return null
    return CoffeeBeanEntity(
        id = id,
        roasterId = roasterId,
        name = name,
        origin = fields.string("origin"),
        process = fields.string("process"),
        roastLevel = fields.string("roastLevel"),
        roastDate = fields.long("roastDate"),
        imageUri = fields.string("imageUri"),
        notes = fields.string("notes"),
        isDeleted = fields.boolean("isDeleted") ?: false,
        createdAt = fields.long("createdAt") ?: updatedAt,
        updatedAt = updatedAt,
    )
}

internal fun RemoteDocument.toGrinderEntity(): GrinderEntity? {
    val brand = fields.string("brand") ?: return null
    val model = fields.string("model") ?: return null
    val updatedAt = fields.long("updatedAt") ?: return null
    return GrinderEntity(
        id = id,
        brand = brand,
        model = model,
        burrType = fields.string("burrType"),
        burrSize = fields.string("burrSize"),
        purchaseDate = fields.long("purchaseDate"),
        burrInstallDate = fields.long("burrInstallDate"),
        imageUri = fields.string("imageUri"),
        notes = fields.string("notes"),
        isDeleted = fields.boolean("isDeleted") ?: false,
        createdAt = fields.long("createdAt") ?: updatedAt,
        updatedAt = updatedAt,
    )
}

internal fun RemoteDocument.toEspressoMachineEntity(): EspressoMachineEntity? {
    val brand = fields.string("brand") ?: return null
    val model = fields.string("model") ?: return null
    val updatedAt = fields.long("updatedAt") ?: return null
    return EspressoMachineEntity(
        id = id,
        brand = brand,
        model = model,
        boilerType = fields.string("boilerType"),
        pumpType = fields.string("pumpType"),
        groupHead = fields.string("groupHead"),
        hasPressureGauge = fields.boolean("hasPressureGauge") ?: false,
        purchaseDate = fields.long("purchaseDate"),
        imageUri = fields.string("imageUri"),
        notes = fields.string("notes"),
        isDeleted = fields.boolean("isDeleted") ?: false,
        createdAt = fields.long("createdAt") ?: updatedAt,
        updatedAt = updatedAt,
    )
}

internal fun RemoteDocument.toFilterBasketEntity(): FilterBasketEntity? {
    val brand = fields.string("brand") ?: return null
    val updatedAt = fields.long("updatedAt") ?: return null
    return FilterBasketEntity(
        id = id,
        brand = brand,
        model = fields.string("model"),
        sizeGrams = fields.string("sizeGrams"),
        sizeClass = fields.string("sizeClass"),
        type = fields.string("type"),
        diameter = fields.string("diameter"),
        purchaseDate = fields.long("purchaseDate"),
        imageUri = fields.string("imageUri"),
        notes = fields.string("notes"),
        isDeleted = fields.boolean("isDeleted") ?: false,
        createdAt = fields.long("createdAt") ?: updatedAt,
        updatedAt = updatedAt,
    )
}

internal fun RemoteDocument.toShotLogEntity(): ShotLogEntity? {
    val coffeeBeanId = fields.string("coffeeBeanId") ?: return null
    val grinderId = fields.string("grinderId") ?: return null
    val machineId = fields.string("machineId") ?: return null
    val basketId = fields.string("basketId") ?: return null
    val doseGrams = fields.double("doseGrams") ?: return null
    val yieldGrams = fields.double("yieldGrams") ?: return null
    val shotAt = fields.long("shotAt") ?: return null
    val updatedAt = fields.long("updatedAt") ?: return null
    return ShotLogEntity(
        id = id,
        coffeeBeanId = coffeeBeanId,
        grinderId = grinderId,
        machineId = machineId,
        basketId = basketId,
        grindSetting = fields.string("grindSetting"),
        doseGrams = doseGrams,
        yieldGrams = yieldGrams,
        extractionTimeSeconds = fields.int("extractionTimeSeconds"),
        brewTemperatureCelsius = fields.double("brewTemperatureCelsius"),
        preInfusionSeconds = fields.int("preInfusionSeconds"),
        rating = fields.int("rating"),
        notes = fields.string("notes"),
        shotAt = shotAt,
        isDeleted = fields.boolean("isDeleted") ?: false,
        createdAt = fields.long("createdAt") ?: updatedAt,
        updatedAt = updatedAt,
    )
}
