package de.nullgrad.pocketband.sensors.model

import androidx.compose.runtime.Immutable

@Immutable
data class SensorEvent(
    val timestamp: Double,
    val dataX: Float,
    val dataY: Float,
    val dataZ: Float,
)