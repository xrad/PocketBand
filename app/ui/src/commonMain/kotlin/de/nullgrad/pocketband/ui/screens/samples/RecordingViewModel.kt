package de.nullgrad.pocketband.ui.screens.samples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.engine.EngineController
import de.nullgrad.pocketband.recorder.RecordingService
import de.nullgrad.pocketband.recorder.usecases.SaveRecordedAudioUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecordingViewModel : ViewModel() {

    private val engineController: EngineController = LOCATOR.get()
    private val recordingService: RecordingService = LOCATOR.get()

    private val saveRecordedAudioUseCase = SaveRecordedAudioUseCase()

    val recordingStats = recordingService.recordingStats

    val audioInputRms = recordingService.audioInputRms.stateIn(scope = viewModelScope,
        started = SharingStarted.Eagerly, initialValue = 0f)

    fun resetRecording() {
        viewModelScope.launch {
            recordingService.resetRecording()
        }
    }

    fun saveRecordingAs(name: String) {
        viewModelScope.launch {
            saveRecordedAudioUseCase(name)
            engineController.stopRecording()
        }
    }

}