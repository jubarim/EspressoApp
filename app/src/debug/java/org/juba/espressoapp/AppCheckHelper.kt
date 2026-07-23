package org.juba.espressoapp

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

/** Installs the debug App Check provider, which logs a token to Logcat for Firebase Console registration. */
internal object AppCheckHelper {
    fun install() {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
    }
}
