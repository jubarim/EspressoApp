package org.juba.espressoapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.dao.EspressoMachineDao
import org.juba.espressoapp.data.local.dao.FilterBasketDao
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.EspressoMachineEntity
import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.GrinderEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity

@Database(
    entities = [
        RoasterEntity::class,
        CoffeeBeanEntity::class,
        GrinderEntity::class,
        EspressoMachineEntity::class,
        FilterBasketEntity::class,
    ],
    version = 6,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roasterDao(): RoasterDao
    abstract fun coffeeBeanDao(): CoffeeBeanDao
    abstract fun grinderDao(): GrinderDao
    abstract fun espressoMachineDao(): EspressoMachineDao
    abstract fun filterBasketDao(): FilterBasketDao
}
