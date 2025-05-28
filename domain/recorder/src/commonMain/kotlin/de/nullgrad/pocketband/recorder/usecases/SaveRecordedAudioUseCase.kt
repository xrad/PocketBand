package de.nullgrad.pocketband.recorder.usecases

import de.nullgrad.pocketband.audio.model.defaultSampleRate
import de.nullgrad.pocketband.audioassets.AudioAssetsRepository
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.recorder.RecordingService

class SaveRecordedAudioUseCase(
    private val recordingService: RecordingService = LOCATOR.get(),
    private val audioAssets: AudioAssetsRepository = LOCATOR.get(),
) {
    suspend operator fun invoke(name: String) {
        val audioData = recordingService.getRecordedAudio()
        audioAssets.saveUserAudioFile("$name.wav", audioData,
            defaultSampleRate
        )
    }
}
