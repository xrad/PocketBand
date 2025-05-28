package de.nullgrad.pocketband.audio.service

import de.nullgrad.pocketband.audio.AudioInput
import de.nullgrad.pocketband.audio.PocketPlayer
import de.nullgrad.pocketband.audio.model.AudioBlock
import de.nullgrad.pocketband.audio.model.RecordingStats
import de.nullgrad.pocketband.audio.model.defaultSampleRate
import de.nullgrad.pocketband.di.LOCATOR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AudioInputImpl : AudioInput {
    companion object {
        fun registerService() {
            LOCATOR.register(AudioInput::class) {
                AudioInputImpl()
            }
        }

        const val NUM_FRAMES = 2048
        const val NUM_CHANNELS = 2
        const val SECTION_SIZE = 1
    }

    private val blocks = mutableListOf<AudioBlock>()

    private var index = 0
    private val bufferSize = NUM_FRAMES * NUM_CHANNELS

    private var recording = false

    private val _recordingStats = MutableStateFlow(RecordingStats.empty)
    override val recordingStats = _recordingStats.asStateFlow()

    override fun start() {
        allocBlock()
        val result = PocketPlayer.instance.startAudio(bufferSize, true)
        if (!result) {
            throw Exception("Audio error")
        }
    }

    private fun allocBlock() {
        blocks.add(
            AudioBlock(
                numFrames = NUM_FRAMES, numChannels = NUM_CHANNELS,
                FloatArray(bufferSize),
                defaultSampleRate, // TODO get from driver
                SECTION_SIZE
            )
        )
    }

    override fun startRecording() {
        recording = true
    }

    override fun stopRecording() {
        recording = false
    }

    override fun processInput() {
        val block = blocks[index]
        PocketPlayer.instance.readBuffer(block.audioData.raw)
        if (recording) {
            index++
            allocBlock()
            updateRecordingStats()
        }
    }

    override fun stop() {
        PocketPlayer.instance.stopAudio()
        _recordingStats.value = getRecordingStats()
    }

    override fun releaseBuffers() {
        index = 0
        val first = blocks.first()
        blocks.clear()
        blocks.add(first)
        _recordingStats.value = RecordingStats.empty
    }

    override fun waitRms() : Float = PocketPlayer.instance.waitRMS()

    override fun getRecordedAudio(): FloatArray {
        return blocks.flattenAudioBlocks()
            .normalizeAudio(maxLevel = 0.5f)
            .compressAudio(sampleRate = defaultSampleRate)
            .normalizeAudio(maxLevel = 0.9f)
    }

    private fun updateRecordingStats() {
        val numSamples = blocks.size * NUM_FRAMES
        val dSamples = numSamples - recordingStats.value.numSamples
        val dt = dSamples.toFloat() / defaultSampleRate
        if (dt > .1) {
            _recordingStats.value = getRecordingStats()
        }
    }

    private fun getRecordingStats() =
        RecordingStats(
            duration = blocks.size * NUM_FRAMES.toFloat() / defaultSampleRate,
            numSamples = blocks.size * NUM_FRAMES,
            numChannels = NUM_CHANNELS,
        )
}