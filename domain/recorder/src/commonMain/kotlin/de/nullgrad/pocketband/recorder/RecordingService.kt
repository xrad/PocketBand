package de.nullgrad.pocketband.recorder

import de.nullgrad.pocketband.audio.model.RecordingStats
import de.nullgrad.pocketband.di.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RecordingService : Service {

    val audioInputRms : Flow<Float>
    val recordingStats : StateFlow<RecordingStats>
    val inputGain : StateFlow<Float>

    fun startInput()
    fun stopInput()
    fun startRecording()
    fun stopRecording()
    fun getRecordedAudio(): FloatArray
    fun resetRecording()
}