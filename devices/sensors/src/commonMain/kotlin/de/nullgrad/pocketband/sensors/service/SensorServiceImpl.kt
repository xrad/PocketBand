package de.nullgrad.pocketband.sensors.service

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.sensors.model.SensorManager
import de.nullgrad.pocketband.sensors.SensorService
import de.nullgrad.pocketband.sensors.platform.getPlatform

internal class SensorServiceImpl : SensorService {
    companion object {
        fun registerService() {
            LOCATOR.register(SensorService::class) {
                SensorServiceImpl()
            }
        }
    }

    override val sensorManager: SensorManager
        get() = getPlatform().sensorManager
}
