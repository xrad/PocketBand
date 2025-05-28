package de.nullgrad.pocketband.audioassets.platform

import de.nullgrad.pocketband.audioassets.model.AudioAsset
import de.nullgrad.pocketband.audioassets.model.AudioFile

internal interface Platform {
    fun initialize()

    suspend fun getAudioAssets(): List<AudioAsset>

    suspend fun loadAudioFile(path: String): AudioFile?

    suspend fun deleteAudioFile(path: String)

    suspend fun saveUserAudioFile(name: String, audioData: FloatArray, sampleRate: Int)
}

internal expect fun getPlatform(): Platform
