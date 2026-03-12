package org.juba.espressoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/** Flat JOIN result returned by [org.juba.espressoapp.data.local.dao.CoffeeBeanDao] queries. */
data class CoffeeBeanWithRoaster(
    @Embedded val bean: CoffeeBeanEntity,
    @ColumnInfo(name = "roaster_name") val roasterName: String?,
)
