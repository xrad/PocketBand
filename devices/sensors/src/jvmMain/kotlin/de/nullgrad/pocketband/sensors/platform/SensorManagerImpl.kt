package de.nullgrad.pocketband.sensors.platform

import de.nullgrad.pocketband.sensors.model.Sensor
import de.nullgrad.pocketband.sensors.model.SensorManager

class SensorManagerImpl() : SensorManager {
    override val sensors: List<Sensor> = emptyList()
}
