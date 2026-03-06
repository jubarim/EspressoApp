package org.juba.espressoapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.entity.RoasterEntity

@Database(
    entities = [RoasterEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roasterDao(): RoasterDao
}
