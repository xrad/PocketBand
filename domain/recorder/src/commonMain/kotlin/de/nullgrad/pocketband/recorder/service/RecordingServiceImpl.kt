@file:OptIn(ExperimentalCoroutinesApi::class)

package de.nullgrad.pocketband.recorder.service

import de.nullgrad.pocketband.audio.AudioInput
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.recorder.RecordingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

internal class RecordingServiceImpl private constructor() : RecordingService
{
    companion object {
        fun registerService() {
            LOCATOR.register(RecordingService::class) {
                RecordingServiceImpl()
            }
        }
    }

    private val audioInput : AudioInput = LOCATOR.get()

    @OptIn(DelicateCoroutinesApi::class)
    private val engineDispatcher = newSingleThreadContext("AudioInput")
    private val engineScope: CoroutineScope = CoroutineScope(SupervisorJob())
    private var engineJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _audioInputRms = MutableSharedFlow<Float>()
    override val audioInputRms = _audioInputRms.asSharedFlow()

    override val recordingStats = audioInput.recordingStats

    private val _inputGain = MutableStateFlow(1f)
    override val inputGain = _inputGain.asStateFlow()

    init {
        watchRMS()
    }

    private fun watchRMS() {
        coroutineScope.launch {
            while (true) {
                val rms = audioInput.waitRms()
                _audioInputRms.emit(rms)
            }
        }
    }

    private fun launchInEngine(block: suspend CoroutineScope.() -> Unit) : Job {
        return engineScope.launch(engineDispatcher, block = block)
    }

    private fun runInEngine(block: suspend CoroutineScope.() -> Unit) {
        return runBlocking(engineDispatcher, block = block)
    }

    override fun startInput() {
        runInEngine {
            audioInput.start()
        }
        engineJob = launchInEngine {
            while (true) {
                audioInput.processInput()
                yield()
            }
        }
    }

    override fun stopInput() {
        runInEngine {
            engineJob?.cancelAndJoin()
            engineJob = null
            audioInput.stop()
            audioInput.releaseBuffers()
        }
    }

    override fun startRecording() {
        runInEngine {
            audioInput.startRecording()
        }
    }

    override fun stopRecording() {
        runInEngine {
            audioInput.stopRecording()
        }
    }

    override fun getRecordedAudio(): FloatArray {
        return audioInput.getRecordedAudio()
    }

    override fun resetRecording() {
        runInEngine {
            audioInput.releaseBuffers()
        }
    }
}
