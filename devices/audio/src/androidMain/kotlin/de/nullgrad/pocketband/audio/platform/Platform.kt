package de.nullgrad.pocketband.audio.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object AndroidPlatform : Platform {
    override fun initialize() {
        runBlocking(Dispatchers.IO) {
            System.loadLibrary("pocketband")
        }
    }
}

internal actual fun getPlatform(): Platform = AndroidPlatform