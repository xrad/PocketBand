package de.nullgrad.pocketband.sensors.platform

import android.app.Application
import de.nullgrad.pocketband.di.AndroidApplication
import de.nullgrad.pocketband.sensors.model.SensorManager

object SensorsAndroidPlatform : Platform {
    private val app: Application get() = AndroidApplication

    override val sensorManager: SensorManager
        get() = SensorManagerImpl(app.applicationContext)
}

internal actual fun getPlatform(): Platform = SensorsAndroidPlatform
