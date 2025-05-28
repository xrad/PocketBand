package de.nullgrad.pocketband.sensors.model

import de.nullgrad.pocketband.sensors.SensorListener

typealias SensorListener = (SensorEvent) -> Unit

interface Sensor {
    val sensorType: SensorType

    fun listen(listener: SensorListener)
    fun unlisten(listener: SensorListener)
}