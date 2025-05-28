package de.nullgrad.pocketband.utils

import de.nullgrad.pocketband.ui.utils.scaleAndMergeChannels
import kotlin.test.Test
import kotlin.test.assertEquals

class WaveformKtTest {
    @Test
    fun testExample() {
        val numSamples = 10
        val ch1 = FloatArray(numSamples) {
            it / numSamples.toFloat()
        }
        val ch2 = FloatArray(numSamples) {
            1 - it / numSamples.toFloat()
        }
        val soundData = listOf(ch1, ch2)
        val scaledWaveData = scaleAndMergeChannels(4, soundData)

        println(scaledWaveData)
        assertEquals(4, scaledWaveData.size)
    }

}