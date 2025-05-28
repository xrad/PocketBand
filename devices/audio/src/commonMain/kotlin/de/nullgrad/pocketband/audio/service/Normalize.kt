package de.nullgrad.pocketband.audio.service

import de.nullgrad.pocketband.audio.model.AudioBlock
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.exp
import kotlin.math.pow

private const val numChannels = 2

private fun List<AudioBlock>.countFrames() : Int {
    var numFrames = 0
    this.forEach {
        numFrames += it.audioData.raw.size / numChannels
    }
    return numFrames
}

internal fun List<AudioBlock>.flattenAudioBlocks() : FloatArray {
    val numFrames = countFrames()
    val flatArray = FloatArray(numFrames * numChannels)
    var flatArrayOffset = 0
    this.forEach {
        val samplesToCopy = it.audioData.raw.size
        it.audioData.raw.copyInto(flatArray, destinationOffset = flatArrayOffset, startIndex = 0, endIndex = samplesToCopy)
        flatArrayOffset += samplesToCopy
    }
    return flatArray
}

private fun FloatArray.getMaxValuesPerChannel() : FloatArray {
    val maxValuePerChannel = FloatArray(size = numChannels, init = {  Float.MIN_VALUE } )
    for (i in indices step numChannels) {
        for (c in 0 until numChannels) {
            val v = this[i + c].absoluteValue
            if (v > maxValuePerChannel[c]) {
                maxValuePerChannel[c] = v
            }
        }
    }
    for (c in 0 until numChannels) {
        if (maxValuePerChannel[c] == 0f) {
            maxValuePerChannel[c] = 1f
        }
    }
    return maxValuePerChannel
}

internal fun FloatArray.normalizeAudio(maxLevel: Float) : FloatArray {
    val maxValuePerChannel = getMaxValuesPerChannel()
    for (i in indices step numChannels) {
        for (c in 0 until numChannels) {
            this[i + c] = this[i + c] / maxValuePerChannel[c] * maxLevel
        }
    }
    return this
}

internal fun FloatArray.compressAudio(
    sampleRate: Int,
    threshold: Float = -20.0f, // in dBFS
    ratio: Float = 4.0f,       // Compression ratio
    attack: Float = 0.01f,     // Attack time in seconds
    release: Float = 0.1f,     // Release time in seconds
    makeupGain: Float = 0.0f   // Makeup gain in dB
): FloatArray {
    // Constants
    val attackCoeff = exp(-1.0 / (sampleRate * attack)).toFloat()
    val releaseCoeff = exp(-1.0 / (sampleRate * release)).toFloat()

    // Convert threshold and makeup gain from dB to linear scale
    val thresholdLinear = 10.0.pow(threshold / 20.0).toFloat()
    val makeupGainLinear = 10.0.pow(makeupGain / 20.0).toFloat()

    // Variables
    var gain = 1.0f

    // Process each sample
    for (i in indices step numChannels) {
        // Calculate the absolute value of the sample
        val absSample = (abs(this[i]) + abs(this[i + 1])) * .5f

        // Determine if compression should be applied
        if (absSample > thresholdLinear) {
            // Calculate the amount of gain reduction
            val targetGain = thresholdLinear + (absSample - thresholdLinear) / ratio

            // Apply attack and release smoothing
            gain = if (targetGain < gain) {
                // Attack (compressing more)
                attackCoeff * (gain - targetGain) + targetGain
            } else {
                // Release (compressing less)
                releaseCoeff * (gain - targetGain) + targetGain
            }
        } else {
            // No compression needed, smoothly return to unity gain
            gain = releaseCoeff * (gain - 1.0f) + 1.0f
        }

        // Apply the computed gain and makeup gain to the sample
        this[i] = this[i] * gain * makeupGainLinear
        this[i+1] = this[i+1] * gain * makeupGainLinear
    }

    return this
}
