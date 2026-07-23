package org.juba.espressoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EspressoAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCheckHelper.install()
    }
}
