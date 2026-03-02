package org.juba.espressoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roasters")
data class RoasterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val country: String?,
    val website: String?,
    @ColumnInfo(name = "image_uri") val imageUri: String?,
    val notes: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
