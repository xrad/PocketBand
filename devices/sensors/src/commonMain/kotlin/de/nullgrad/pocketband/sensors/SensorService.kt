package de.nullgrad.pocketband.sensors

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.sensors.model.SensorManager

interface SensorService : Service {
    val sensorManager : SensorManager
}
