package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.audio.AudioOutput
import de.nullgrad.pocketband.audio.model.AudioBlock
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.audio.model.defaultSampleRate
import de.nullgrad.pocketband.di.LOCATOR
import kotlinx.coroutines.channels.Channel

class MockAudioOutput : AudioOutput {
    companion object {
        fun registerService() {
            LOCATOR.register(AudioOutput::class) {
                MockAudioOutput()
            }
        }
    }

    enum class Command {
        TriggerOutputCycle,
    }

    val commandChannel = Channel<Command>()

    lateinit var audioBlock : AudioBlock
        private set

    init {
        setAudioDataSize(100, 1)
    }

    override var sampleRate: Int = defaultSampleRate

    fun setAudioDataSize(
        numFrames: Int,
        numChannels: Int,
        sampleRate: Int = defaultSampleRate,
    ) {
        audioBlock = AudioBlock(
            sampleRate = sampleRate,
            numFrames = numFrames,
            numChannels = numChannels,
            numSectionFrames = numFrames,
            buffer = FloatArray(numFrames),
        )
    }

    override fun start() {
    }

    override suspend fun processOutput(fillBuffer: suspend (AudioData) -> Unit) {
        val command = commandChannel.receive()
        println("command: $command")
        if (command == Command.TriggerOutputCycle) {
            fillBuffer(audioBlock.audioData)
        }
    }

    override fun stop() {
    }
}