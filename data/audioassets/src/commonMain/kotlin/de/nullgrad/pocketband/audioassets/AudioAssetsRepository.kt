package de.nullgrad.pocketband.audioassets

import de.nullgrad.pocketband.audioassets.model.AudioAsset
import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.di.Service
import kotlinx.coroutines.flow.StateFlow

interface AudioAssetsRepository : Service {
    val assets: StateFlow<List<AudioAsset>>

    suspend fun loadAudioFile(path: String): AudioFile?

    suspend fun deleteAudioFile(path: String)

    suspend fun saveUserAudioFile(name: String, audioData: FloatArray, sampleRate: Int)
}
