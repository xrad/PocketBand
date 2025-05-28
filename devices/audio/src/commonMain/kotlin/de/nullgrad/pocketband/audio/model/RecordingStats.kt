package de.nullgrad.pocketband.audio.model

import androidx.compose.runtime.Immutable

@Immutable
data class RecordingStats(
    val duration: Float,
    val numSamples: Int,
    val numChannels: Int,
) {
    companion object {
        val empty = RecordingStats(
            duration = 0f,
            numSamples = 0,
            numChannels = 0
        )
    }
}

