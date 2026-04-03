package org.juba.espressoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shot_logs",
    foreignKeys = [
        ForeignKey(
            entity = CoffeeBeanEntity::class,
            parentColumns = ["id"],
            childColumns = ["coffee_bean_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = GrinderEntity::class,
            parentColumns = ["id"],
            childColumns = ["grinder_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = EspressoMachineEntity::class,
            parentColumns = ["id"],
            childColumns = ["machine_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = FilterBasketEntity::class,
            parentColumns = ["id"],
            childColumns = ["basket_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("coffee_bean_id"),
        Index("grinder_id"),
        Index("machine_id"),
        Index("basket_id"),
    ],
)
data class ShotLogEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "coffee_bean_id") val coffeeBeanId: String,
    @ColumnInfo(name = "grinder_id") val grinderId: String,
    @ColumnInfo(name = "machine_id") val machineId: String,
    @ColumnInfo(name = "basket_id") val basketId: String,
    @ColumnInfo(name = "grind_setting") val grindSetting: String?,
    @ColumnInfo(name = "dose_grams") val doseGrams: Double,
    @ColumnInfo(name = "yield_grams") val yieldGrams: Double,
    @ColumnInfo(name = "extraction_time_seconds") val extractionTimeSeconds: Int?,
    @ColumnInfo(name = "brew_temperature_celsius") val brewTemperatureCelsius: Double?,
    @ColumnInfo(name = "pre_infusion_seconds") val preInfusionSeconds: Int?,
    val rating: Int?,
    val notes: String?,
    @ColumnInfo(name = "shot_at") val shotAt: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
