package de.nullgrad.pocketband.plugins.toolbox

import kotlin.math.floor
import kotlin.math.sin

enum class WaveTableType(val label: String) {
    Sine("Sine"),
    Triangle("Triangle"),
    Saw("Saw");

    companion object {
        fun byString(s: String): WaveTableType {
            return values().firstOrNull { it.label == s } ?: throw IllegalArgumentException("Not found")
        }
    }
}

data class WaveTable(val size: Int) {

    private val waveTable = DoubleArray(size)

    fun config(type: WaveTableType, bipolar: Boolean, amp: Double) {
        when (type) {
            WaveTableType.Sine -> sine(min = if (bipolar) -amp else 0.0, max = amp)
            WaveTableType.Triangle -> triangle(min = if (bipolar) -amp else 0.0, max = amp)
            WaveTableType.Saw -> saw(min = if (bipolar) -amp else 0.0, max = amp)
        }
    }

    fun sine(min: Double = -1.0, max: Double = 1.0) {
        // Calculate scale and offset for desired range
        val scale = (max - min) / 2.0
        val offset = (min + max) / 2.0
        val segment = 2.0 * Math.PI / size

        // Generate sine wave data point-by-point
        for (i in 0 until size) {
            waveTable[i] = sin(segment * i) * scale + offset
        }
    }

    fun triangle(min: Double = -1.0, max: Double = 1.0) {
        val m = (max - min) / (size / 2.0)
        var y = (max + min) / 2.0
        for (i in 0 until size / 4) {
            waveTable[i] = y
            y += m
        }
        for (i in size / 4 until 3 * size / 4) {
            waveTable[i] = y
            y -= m
        }
        for (i in 3 * size / 4 until size) {
            waveTable[i] = y
            y += m
        }
    }

    fun saw(min: Double = -1.0, max: Double = 1.0) {
        val m = -(max - min) / size
        var y = max
        for (i in 0 until size) {
            waveTable[i] = y
            y += m
        }
    }

    operator fun get(index: Int): Double = waveTable[index]

    operator fun set(index: Int, value: Double) {
        waveTable[index] = value
    }

    fun getSample(phase: Double): Double {
        val index = floor(phase * size).toInt().coerceIn(0, size - 1)
        return this[index]
    }
}
