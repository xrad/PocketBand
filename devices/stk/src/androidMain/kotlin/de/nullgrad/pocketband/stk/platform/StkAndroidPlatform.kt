package de.nullgrad.pocketband.stk.platform

import android.app.Application
import de.nullgrad.pocketband.di.AndroidApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object StkAndroidPlatform : Platform {
    private val app: Application get() = AndroidApplication

    override fun initialize() {
        runBlocking(Dispatchers.IO) {
            System.loadLibrary("stk")
            setupStkAssets(app.applicationContext)
        }
    }
}

internal actual fun getPlatform(): Platform = StkAndroidPlatform

