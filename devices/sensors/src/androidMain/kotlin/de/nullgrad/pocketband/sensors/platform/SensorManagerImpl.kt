package de.nullgrad.pocketband.sensors.platform

import android.content.Context
import de.nullgrad.pocketband.sensors.model.Sensor
import de.nullgrad.pocketband.sensors.model.SensorEvent
import de.nullgrad.pocketband.sensors.SensorListener
import de.nullgrad.pocketband.sensors.model.SensorManager
import de.nullgrad.pocketband.sensors.model.SensorType

internal class SensorImpl(
    override val sensorType: SensorType,
    private val sensor: android.hardware.Sensor,
    private val manager: android.hardware.SensorManager,
) : Sensor, android.hardware.SensorEventListener {

    private val listeners = mutableListOf<SensorListener>()

    override fun listen(listener: SensorListener) {
        listeners.add(listener)
        if (listeners.size == 1) {
            manager.registerListener(this, sensor,
                android.hardware.SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun unlisten(listener: SensorListener) {
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            manager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: android.hardware.SensorEvent?) {
        if (event == null) return
        val ev = SensorEvent(
            timestamp = event.timestamp * NS2S,
            dataX = event.values[0],
            dataY = event.values[1],
            dataZ = event.values[2],
        )
        listeners.forEach { it(ev) }
    }

    override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {
    }

    companion object {
        private const val NS2S = 1.0 / 1000000000.0
    }
}

class SensorManagerImpl(context: Context) : SensorManager {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as android.hardware.SensorManager

    override val sensors: List<Sensor> by lazy {
        sensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL).mapNotNull {
            if (it.type == android.hardware.Sensor.TYPE_GRAVITY) {
                SensorImpl(sensorType = SensorType.Gravity, sensor = it, manager = sensorManager)
            }
            else if (it.type == android.hardware.Sensor.TYPE_ACCELEROMETER) {
                SensorImpl(sensorType = SensorType.Accelerometer, sensor = it, manager = sensorManager)
            }
            else {
                null
            }
        }
    }

}
