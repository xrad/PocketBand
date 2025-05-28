package de.nullgrad.pocketband.engine.service

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.recorder.RecordingService
import de.nullgrad.pocketband.synth.SynthService
import de.nullgrad.pocketband.engine.EngineController
import de.nullgrad.pocketband.engine.EngineMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class EngineControllerImpl private constructor(
    private val synthService: SynthService = LOCATOR.get(),
    private val recordingService: RecordingService = LOCATOR.get(),
): EngineController {

    companion object {
        fun registerService() {
            LOCATOR.register(EngineController::class) {
                EngineControllerImpl()
            }
        }
    }

    private val _engineMode = MutableStateFlow(EngineMode.Idle)
    override val engineMode = _engineMode.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun startRecording() {
        recordingService.startRecording()
        _engineMode.value = EngineMode.Recording
    }

    override fun stopRecording() {
        recordingService.stopRecording()
        _engineMode.value = EngineMode.InputMonitor
    }

    override fun startOutput() {
        if (_engineMode.value == EngineMode.Playing) {
            return
        }
        coroutineScope.launch {
            if (_engineMode.value == EngineMode.Recording) {
                recordingService.stopRecording()
                recordingService.stopInput()
            }
            if (_engineMode.value == EngineMode.InputMonitor) {
                recordingService.stopInput()
            }
            synthService.startOutput()
            _engineMode.value = EngineMode.Playing
        }
    }

    override fun startInputMonitor() {
        if (_engineMode.value == EngineMode.InputMonitor) {
            return
        }
        if (_engineMode.value == EngineMode.Recording) {
            return
        }
        coroutineScope.launch {
            if (_engineMode.value == EngineMode.Playing) {
                synthService.stopOutput()
            }
            recordingService.startInput()
            _engineMode.value = EngineMode.InputMonitor
        }
    }

    private var previousEngineMode = EngineMode.Playing

    override fun pauseEngine() {
        previousEngineMode = _engineMode.value
        stopEngine()
    }

    override fun resumeEngine() {
        when (previousEngineMode) {
            EngineMode.Playing -> startOutput()
            EngineMode.InputMonitor -> startInputMonitor()
            EngineMode.Recording -> startInputMonitor()
            EngineMode.Idle -> { }
        }
    }

    override fun stopEngine() {
        when (_engineMode.value) {
            EngineMode.Playing -> {
                synthService.stopOutput()
            }
            EngineMode.InputMonitor -> {
                recordingService.stopInput()
            }
            EngineMode.Recording -> {
                recordingService.stopRecording()
                recordingService.stopInput()
            }
            else -> {

            }
        }
        _engineMode.value = EngineMode.Idle
    }
}
