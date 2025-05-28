package de.nullgrad.pocketband.audio.service

import de.nullgrad.pocketband.audio.AudioOutput
import de.nullgrad.pocketband.audio.PocketPlayer
import de.nullgrad.pocketband.audio.model.AudioBlock
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.LOCATOR

internal class AudioOutputImpl : AudioOutput {

    companion object {
        fun registerService() {
            LOCATOR.register(AudioOutput::class) {
                AudioOutputImpl()
            }
        }

        const val NUM_FRAMES = 2048
        const val SECTION_SIZE = 128
        const val NUM_CHANNELS = 2
    }

    private val blocks = mutableListOf<AudioBlock>()

    private var index = 0

    override val sampleRate get() = PocketPlayer.instance.getSampleRate()

    override fun start() {
        val bufferSize = NUM_FRAMES * NUM_CHANNELS
        val result = PocketPlayer.instance.startAudio(bufferSize, false)
        if (!result) {
            throw Exception("Audio error")
        }
        val sampleRate = PocketPlayer.instance.getSampleRate()
        blocks.clear()
        blocks.add(
            AudioBlock(
                numFrames = NUM_FRAMES, numChannels = NUM_CHANNELS,
                FloatArray(bufferSize),
                sampleRate,
                SECTION_SIZE
            )
        )
        blocks.add(
            AudioBlock(
                numFrames = NUM_FRAMES, numChannels = NUM_CHANNELS,
                FloatArray(bufferSize),
                sampleRate,
                SECTION_SIZE
            )
        )
    }

    override suspend fun processOutput(fillBuffer: suspend (AudioData) -> Unit) {
        if (blocks.isEmpty()) {
            return
        }
        val block = blocks[index]
        for (buffer in block.sections) {
            fillBuffer(buffer)
        }
        PocketPlayer.instance.writeBuffer(block.audioData.raw)
        index = (index + 1) % 2
    }

    override fun stop() {
        PocketPlayer.instance.stopAudio()
        blocks.clear()
    }
}