package de.nullgrad.pocketband.synth.service

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.audioassets.model.AudioFile

internal class AudioFilePlayback(
    private val audioFile: AudioFile,
) {
    private var samplePos: Int = 0

    fun addToBuffer(buffer: AudioData) : Boolean {
        for (i in 0 until buffer.numFrames) {
            if (samplePos >= audioFile.numSamples) {
                return false
            }
            if (audioFile.numChannels == 1) {
                val s = audioFile.soundData[0][samplePos]
                buffer[i * 2] += s
                buffer[i * 2 + 1] += s
            }
            else if (audioFile.numChannels == 2) {
                buffer[i * 2]     += audioFile.soundData[0][samplePos]
                buffer[i * 2 + 1] += audioFile.soundData[1][samplePos]
            }
            samplePos++
        }
        return true
    }
}