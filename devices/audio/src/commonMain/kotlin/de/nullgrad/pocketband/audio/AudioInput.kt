package de.nullgrad.pocketband.audio

import de.nullgrad.pocketband.audio.model.RecordingStats
import de.nullgrad.pocketband.di.Service
import kotlinx.coroutines.flow.StateFlow

interface AudioInput : Service {
    val recordingStats : StateFlow<RecordingStats>

    fun start()
    fun startRecording()
    fun stopRecording()
    fun processInput()
    fun stop()
    fun releaseBuffers()
    fun waitRms() : Float
    fun getRecordedAudio(): FloatArray
}
