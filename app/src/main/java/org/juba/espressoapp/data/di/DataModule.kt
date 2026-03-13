package org.juba.espressoapp.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.juba.espressoapp.BuildConfig
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.local.seed.DatabaseSeedCallback
import org.juba.espressoapp.data.repository.CoffeeBeanRepositoryImpl
import org.juba.espressoapp.data.repository.GrinderRepositoryImpl
import org.juba.espressoapp.data.repository.RoasterRepositoryImpl
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import org.juba.espressoapp.domain.repository.GrinderRepository
import org.juba.espressoapp.domain.repository.RoasterRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindRoasterRepository(impl: RoasterRepositoryImpl): RoasterRepository

    @Binds
    @Singleton
    abstract fun bindCoffeeBeanRepository(impl: CoffeeBeanRepositoryImpl): CoffeeBeanRepository

    @Binds
    @Singleton
    abstract fun bindGrinderRepository(impl: GrinderRepositoryImpl): GrinderRepository

    companion object {

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS coffee_beans (
                        id TEXT NOT NULL PRIMARY KEY,
                        roaster_id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        origin TEXT,
                        process TEXT,
                        roast_level TEXT,
                        roast_date INTEGER,
                        image_uri TEXT,
                        notes TEXT,
                        is_deleted INTEGER NOT NULL DEFAULT 0,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL,
                        FOREIGN KEY(roaster_id) REFERENCES roasters(id) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_coffee_beans_roaster_id ON coffee_beans(roaster_id)",
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS grinders (
                        id TEXT NOT NULL PRIMARY KEY,
                        brand TEXT NOT NULL,
                        model TEXT NOT NULL,
                        burr_type TEXT,
                        burr_size TEXT,
                        purchase_date INTEGER,
                        burr_install_date INTEGER,
                        image_uri TEXT,
                        notes TEXT,
                        is_deleted INTEGER NOT NULL DEFAULT 0,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "espresso_app.db",
            ).fallbackToDestructiveMigration(true)
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                .apply { if (BuildConfig.DEBUG) addCallback(DatabaseSeedCallback) }
                .build()

        @Provides
        fun provideRoasterDao(db: AppDatabase): RoasterDao = db.roasterDao()

        @Provides
        fun provideCoffeeBeanDao(db: AppDatabase): CoffeeBeanDao = db.coffeeBeanDao()

        @Provides
        fun provideGrinderDao(db: AppDatabase): GrinderDao = db.grinderDao()
    }
}
