package de.nullgrad.pocketband.sensors

import de.nullgrad.pocketband.sensors.service.SensorServiceImpl

object SensorsModule {
    fun initialize() {
        SensorServiceImpl.registerService()
    }
}
