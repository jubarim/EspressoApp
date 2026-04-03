package org.juba.espressoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/** Flat JOIN result returned by [org.juba.espressoapp.data.local.dao.ShotLogDao] queries. */
data class ShotLogWithDetails(
    @Embedded val shot: ShotLogEntity,
    @ColumnInfo(name = "coffee_bean_name") val coffeeBeanName: String?,
    @ColumnInfo(name = "roaster_name") val roasterName: String?,
    @ColumnInfo(name = "grinder_name") val grinderName: String?,
    @ColumnInfo(name = "machine_name") val machineName: String?,
    @ColumnInfo(name = "basket_name") val basketName: String?,
)
