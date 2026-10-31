package org.juba.espressoapp.logging

import android.util.Log
import org.juba.espressoapp.BuildConfig

/**
 * Thin wrapper over [android.util.Log] used across the project.
 *
 * Verbose, debug and info messages are stripped outside debug builds. Warnings and errors are
 * always emitted so production problems still leave a trace in logcat.
 *
 * Messages are lambdas so the string is only built when the level is enabled. This keeps
 * logging out of hot paths such as a per-document sync loop.
 *
 * Usage:
 * ```
 * AppLog.d(TAG) { "pushed ${documents.size} documents" }
 * AppLog.e(TAG, error) { "push failed" }
 * ```
 */
object AppLog {
    private const val PRE_TAG = "jm_"

    fun v(tag: String, message: () -> String) {
        if (BuildConfig.DEBUG) Log.v(PRE_TAG + tag, message())
    }

    fun d(tag: String, message: () -> String) {
        if (BuildConfig.DEBUG) Log.d(PRE_TAG + tag, message())
    }

    fun i(tag: String, message: () -> String) {
        if (BuildConfig.DEBUG) Log.i(PRE_TAG + tag, message())
    }

    fun w(tag: String, error: Throwable? = null, message: () -> String) {
        Log.w(PRE_TAG + tag, message(), error)
    }

    fun e(tag: String, error: Throwable? = null, message: () -> String) {
        Log.e(PRE_TAG + tag, message(), error)
    }
}
