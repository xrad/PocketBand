package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.audioassets.AudioAssetsRepository
import de.nullgrad.pocketband.audioassets.model.AudioAsset
import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.di.LOCATOR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockAudioAssetsRepository : AudioAssetsRepository {
    companion object {
        fun registerService() {
            LOCATOR.register(AudioAssetsRepository::class) {
                MockAudioAssetsRepository()
            }
        }
    }

    override val assets: StateFlow<List<AudioAsset>>
        get() = MutableStateFlow(emptyList())

    override suspend fun loadAudioFile(path: String): AudioFile? {
        return null
    }

    override suspend fun deleteAudioFile(path: String) {
    }

    override suspend fun saveUserAudioFile(name: String, audioData: FloatArray, sampleRate: Int) {
    }
}