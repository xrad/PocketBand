package de.nullgrad.pocketband.sensors.platform

import de.nullgrad.pocketband.sensors.model.SensorManager

object SensorsDesktopPlatform : Platform {
    override val sensorManager: SensorManager
        get() = SensorManagerImpl()
}

internal actual fun getPlatform(): Platform = SensorsDesktopPlatform
