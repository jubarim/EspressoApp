package org.juba.espressoapp.data.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.juba.espressoapp.BuildConfig
import org.juba.espressoapp.data.remote.FirestoreRemoteSource
import org.juba.espressoapp.data.remote.FirestoreSyncTarget
import org.juba.espressoapp.data.remote.RemoteSource
import org.juba.espressoapp.data.remote.SyncTarget
import org.juba.espressoapp.data.sync.FirestoreRemoteWriteDispatcher
import org.juba.espressoapp.data.sync.RemoteWriteDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncTarget(impl: FirestoreSyncTarget): SyncTarget

    @Binds
    @Singleton
    abstract fun bindRemoteSource(impl: FirestoreRemoteSource): RemoteSource

    @Binds
    @Singleton
    abstract fun bindRemoteWriteDispatcher(impl: FirestoreRemoteWriteDispatcher): RemoteWriteDispatcher

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore {
            FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)
            return FirebaseFirestore.getInstance()
        }
    }
}
