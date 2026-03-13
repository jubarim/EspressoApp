package org.juba.espressoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_baskets")
data class FilterBasketEntity(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String?,
    @ColumnInfo(name = "size_grams") val sizeGrams: String?,
    val type: String?,
    val diameter: String?,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Long?,
    @ColumnInfo(name = "image_uri") val imageUri: String?,
    val notes: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
