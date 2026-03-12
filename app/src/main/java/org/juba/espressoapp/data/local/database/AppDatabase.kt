package org.juba.espressoapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity

@Database(
    entities = [RoasterEntity::class, CoffeeBeanEntity::class],
    version = 3,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roasterDao(): RoasterDao
    abstract fun coffeeBeanDao(): CoffeeBeanDao
}
