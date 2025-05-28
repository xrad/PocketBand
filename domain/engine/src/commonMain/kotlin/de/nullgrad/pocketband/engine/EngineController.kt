package de.nullgrad.pocketband.engine

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.engine.service.EngineControllerImpl
import kotlinx.coroutines.flow.StateFlow

enum class EngineMode {
    Idle,
    Playing,
    InputMonitor,
    Recording,
}

interface EngineController : Service {
    val engineMode: StateFlow<EngineMode>

    fun startRecording()
    fun stopRecording()
    fun startOutput()
    fun startInputMonitor()
    fun pauseEngine()
    fun resumeEngine()
    fun stopEngine()
}

object EngineModule {
    fun initialize() {
        EngineControllerImpl.registerService()
    }
}