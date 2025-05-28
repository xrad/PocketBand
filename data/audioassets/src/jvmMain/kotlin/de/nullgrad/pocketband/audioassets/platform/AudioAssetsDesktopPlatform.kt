package de.nullgrad.pocketband.audioassets.platform

import de.nullgrad.pocketband.audioassets.model.AudioAsset
import de.nullgrad.pocketband.audioassets.model.AudioFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

object AudioAssetsAndroidPlatform : Platform {

    override fun initialize() {
        runBlocking(Dispatchers.IO) {
//            File(getUserWavDir(app.applicationContext)).mkdirs()
//            syncAudioAssets(app.applicationContext)
        }
    }

    override suspend fun getAudioAssets(): List<AudioAsset> =
        withContext(Dispatchers.IO) {
            //getAudioAssets(app)
            emptyList()
        }

    override suspend fun loadAudioFile(path: String): AudioFile? =
        withContext(Dispatchers.IO) {
            if (path.lowercase().endsWith(".wav")) {
//                return@withContext loadWavFile(path)
            }
            throw Exception("unknown audio asset format")
        }

    override suspend fun saveUserAudioFile(
        name: String,
        audioData: FloatArray,
        sampleRate: Int
    ) = withContext(Dispatchers.IO) {
        if (name.lowercase().endsWith(".wav")) {
//            val path = File(getUserWavDir(app), name).absolutePath
//            saveWavFile(path, audioData, sampleRate)
            return@withContext
        }
        throw Exception("unknown audio asset format")
    }

    override suspend fun deleteAudioFile(path: String) : Unit =
        withContext(Dispatchers.IO) {
            try {
                File(path).delete()
            }
            catch (_: Exception) {
            }
        }


}

internal actual fun getPlatform(): Platform = AudioAssetsAndroidPlatform
