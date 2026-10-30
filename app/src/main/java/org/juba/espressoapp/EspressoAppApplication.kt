package org.juba.espressoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.juba.espressoapp.data.sync.SyncEngine
import javax.inject.Inject

@HiltAndroidApp
class EspressoAppApplication : Application() {

    @Inject
    lateinit var syncEngine: SyncEngine

    override fun onCreate() {
        super.onCreate()
        syncEngine.start()
    }
}
