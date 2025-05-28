package de.nullgrad.pocketband.sensors.platform

import de.nullgrad.pocketband.sensors.model.SensorManager

internal interface Platform {
    val sensorManager: SensorManager
}

internal expect fun getPlatform(): Platform
