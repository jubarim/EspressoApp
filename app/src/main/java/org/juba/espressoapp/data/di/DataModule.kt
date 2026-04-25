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
import org.juba.espressoapp.data.local.dao.EspressoMachineDao
import org.juba.espressoapp.data.local.dao.FilterBasketDao
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.dao.ShotLogDao
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.local.seed.DatabaseSeedCallback
import org.juba.espressoapp.data.repository.BackupRepositoryImpl
import org.juba.espressoapp.data.repository.CoffeeBeanRepositoryImpl
import org.juba.espressoapp.data.repository.EspressoMachineRepositoryImpl
import org.juba.espressoapp.data.repository.FilterBasketRepositoryImpl
import org.juba.espressoapp.data.repository.GrinderRepositoryImpl
import org.juba.espressoapp.data.repository.RoasterRepositoryImpl
import org.juba.espressoapp.data.repository.ShotLogRepositoryImpl
import org.juba.espressoapp.domain.repository.BackupRepository
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import org.juba.espressoapp.domain.repository.GrinderRepository
import org.juba.espressoapp.domain.repository.RoasterRepository
import org.juba.espressoapp.domain.repository.ShotLogRepository
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

    @Binds
    @Singleton
    abstract fun bindEspressoMachineRepository(impl: EspressoMachineRepositoryImpl): EspressoMachineRepository

    @Binds
    @Singleton
    abstract fun bindFilterBasketRepository(impl: FilterBasketRepositoryImpl): FilterBasketRepository

    @Binds
    @Singleton
    abstract fun bindShotLogRepository(impl: ShotLogRepositoryImpl): ShotLogRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository

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

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS espresso_machines (
                        id TEXT NOT NULL PRIMARY KEY,
                        brand TEXT NOT NULL,
                        model TEXT NOT NULL,
                        boiler_type TEXT,
                        pump_type TEXT,
                        group_head TEXT,
                        has_pressure_gauge INTEGER NOT NULL DEFAULT 0,
                        purchase_date INTEGER,
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

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `filter_baskets` (
                        `id` TEXT NOT NULL,
                        `brand` TEXT NOT NULL,
                        `model` TEXT,
                        `size_grams` TEXT,
                        `type` TEXT,
                        `diameter` TEXT,
                        `purchase_date` INTEGER,
                        `image_uri` TEXT,
                        `notes` TEXT,
                        `is_deleted` INTEGER NOT NULL DEFAULT 0,
                        `created_at` INTEGER NOT NULL,
                        `updated_at` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `shot_logs` (
                        `id` TEXT NOT NULL,
                        `coffee_bean_id` TEXT NOT NULL,
                        `grinder_id` TEXT NOT NULL,
                        `machine_id` TEXT NOT NULL,
                        `basket_id` TEXT NOT NULL,
                        `grind_setting` TEXT,
                        `dose_grams` REAL NOT NULL,
                        `yield_grams` REAL NOT NULL,
                        `extraction_time_seconds` INTEGER,
                        `brew_temperature_celsius` REAL,
                        `pre_infusion_seconds` INTEGER,
                        `rating` INTEGER,
                        `notes` TEXT,
                        `shot_at` INTEGER NOT NULL,
                        `is_deleted` INTEGER NOT NULL DEFAULT 0,
                        `created_at` INTEGER NOT NULL,
                        `updated_at` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`coffee_bean_id`) REFERENCES `coffee_beans`(`id`) ON DELETE RESTRICT,
                        FOREIGN KEY(`grinder_id`) REFERENCES `grinders`(`id`) ON DELETE RESTRICT,
                        FOREIGN KEY(`machine_id`) REFERENCES `espresso_machines`(`id`) ON DELETE RESTRICT,
                        FOREIGN KEY(`basket_id`) REFERENCES `filter_baskets`(`id`) ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_shot_logs_coffee_bean_id` ON `shot_logs`(`coffee_bean_id`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_shot_logs_grinder_id` ON `shot_logs`(`grinder_id`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_shot_logs_machine_id` ON `shot_logs`(`machine_id`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_shot_logs_basket_id` ON `shot_logs`(`basket_id`)")
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
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                .apply { if (BuildConfig.DEBUG) addCallback(DatabaseSeedCallback) }
                .build()

        @Provides
        fun provideRoasterDao(db: AppDatabase): RoasterDao = db.roasterDao()

        @Provides
        fun provideCoffeeBeanDao(db: AppDatabase): CoffeeBeanDao = db.coffeeBeanDao()

        @Provides
        fun provideGrinderDao(db: AppDatabase): GrinderDao = db.grinderDao()

        @Provides
        fun provideEspressoMachineDao(db: AppDatabase): EspressoMachineDao = db.espressoMachineDao()

        @Provides
        fun provideFilterBasketDao(db: AppDatabase): FilterBasketDao = db.filterBasketDao()

        @Provides
        fun provideShotLogDao(db: AppDatabase): ShotLogDao = db.shotLogDao()
    }
}
