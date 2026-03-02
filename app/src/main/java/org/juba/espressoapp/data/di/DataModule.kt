package org.juba.espressoapp.data.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.repository.RoasterRepositoryImpl
import org.juba.espressoapp.domain.repository.RoasterRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindRoasterRepository(impl: RoasterRepositoryImpl): RoasterRepository

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "espresso_app.db",
            ).build()

        @Provides
        fun provideRoasterDao(db: AppDatabase): RoasterDao = db.roasterDao()
    }
}
