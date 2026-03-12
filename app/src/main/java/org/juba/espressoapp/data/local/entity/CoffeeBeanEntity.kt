package org.juba.espressoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "coffee_beans",
    foreignKeys = [
        ForeignKey(
            entity = RoasterEntity::class,
            parentColumns = ["id"],
            childColumns = ["roaster_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("roaster_id")],
)
data class CoffeeBeanEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "roaster_id") val roasterId: String,
    val name: String,
    val origin: String?,
    val process: String?,
    @ColumnInfo(name = "roast_level") val roastLevel: String?,
    @ColumnInfo(name = "roast_date") val roastDate: Long?,
    @ColumnInfo(name = "image_uri") val imageUri: String?,
    val notes: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
