package org.juba.espressoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "espresso_machines")
data class EspressoMachineEntity(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String,
    @ColumnInfo(name = "boiler_type") val boilerType: String?,
    @ColumnInfo(name = "pump_type") val pumpType: String?,
    @ColumnInfo(name = "group_head") val groupHead: String?,
    @ColumnInfo(name = "has_pressure_gauge") val hasPressureGauge: Boolean = false,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Long?,
    @ColumnInfo(name = "image_uri") val imageUri: String?,
    val notes: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
