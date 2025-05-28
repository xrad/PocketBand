package de.nullgrad.pocketband.audio.model

class AudioData(
    private val startOffset : Int,
    val numFrames: Int,
    val numChannels: Int,
    private val buffer: FloatArray,
    val sampleRate: Int
) {
    operator fun get(index: Int): Float {
        return buffer[startOffset * numChannels + index]
    }

    operator fun set(index: Int, value: Float) {
        buffer[startOffset * numChannels + index] = value
    }

    val raw = buffer
    val rawOffset = startOffset * numChannels

    fun clear() {
        val size = numFrames * numChannels
        for (i in 0 until size) {
            buffer[startOffset * numChannels + i] = 0.0f
        }
    }

    fun slice(startFrame: Int, sliceFrames: Int): AudioData {
        if (startFrame == 0 && sliceFrames == numFrames) {
            return this
        }
        require(startFrame >= 0) { "Start frame cannot be negative" }
        require((startFrame + sliceFrames) <= numFrames) { "Slice exceeds audio data size" }
        return AudioData(startFrame, sliceFrames, numChannels, buffer, sampleRate)
    }

    fun split(atFrame: Int): Pair<AudioData, AudioData> {
        require(atFrame >= 0) { "Split frame cannot be negative" }
        require(atFrame < numFrames) { "Split frame must be within audio data size" }
        return Pair(
            AudioData(startOffset, atFrame, numChannels, buffer, sampleRate),
            AudioData(startOffset + atFrame, numFrames - atFrame, numChannels, buffer, sampleRate)
        )
    }
}