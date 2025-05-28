package de.nullgrad.pocketband.audioassets.model

import androidx.compose.runtime.Immutable

@Immutable
data class AudioFile(
    val path: String,
    val soundData: List<FloatArray>,
    val sampleRate: Int
) {
    val numSamples: Int
        get() = soundData[0].size

    val numChannels: Int
        get() = soundData.size

    val duration: Double
        get() = numSamples / sampleRate.toDouble()
}
