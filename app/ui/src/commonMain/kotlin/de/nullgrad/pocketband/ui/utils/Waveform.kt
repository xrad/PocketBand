package de.nullgrad.pocketband.ui.utils

import kotlin.math.abs
import kotlin.math.sqrt

fun scaleAndMergeChannels(
    numScaledSamples: Int,
    soundData: List<FloatArray>): DoubleArray {

    val numInputSamples = soundData[0].size
    require(numInputSamples != 0)

    val numChannels = soundData.size
    require(numChannels != 0)

    val scaledWaveData = DoubleArray(numScaledSamples)
    val scalingFactor = numInputSamples.toDouble() / numScaledSamples.toDouble()
    val blockSize = scalingFactor.toInt()

    repeat(numScaledSamples) { outIndex ->
        val outStart = (outIndex * scalingFactor).toInt()
        val outEnd = (outStart + blockSize).coerceAtMost(numInputSamples - 1)
        var blockSumOfSquares = 0.0
        var maxSample = 0.0f
        for(inIndex in outStart until outEnd) {
            repeat(numChannels) { ch ->
                maxSample = maxOf(maxSample, abs(soundData[ch][inIndex]))
            }
            blockSumOfSquares += maxSample * maxSample
        }
        val rms = sqrt(blockSumOfSquares / blockSize)
        scaledWaveData[outIndex] = rms
    }
    return scaledWaveData
}
