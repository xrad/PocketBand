package de.nullgrad.pocketband.audioassets.service

import de.nullgrad.pocketband.audioassets.AudioAssetsRepository
import de.nullgrad.pocketband.audioassets.model.AudioAsset
import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.audioassets.platform.getPlatform
import de.nullgrad.pocketband.di.IoDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class AudioAssetsRepositoryImpl private constructor(
    private val ioDispatcher: CoroutineContext = LOCATOR.get(IoDispatcher::class)
) : AudioAssetsRepository {
    private val _assets = MutableStateFlow<List<AudioAsset>>(emptyList())
    override val assets: StateFlow<List<AudioAsset>> = _assets

    companion object {
        fun registerService() {
            LOCATOR.register(AudioAssetsRepository::class) {
                AudioAssetsRepositoryImpl()
            }
        }
    }

    init {
        CoroutineScope(ioDispatcher).launch {
            syncAssets()
        }
    }

    private suspend fun syncAssets() {
        _assets.value = getPlatform().getAudioAssets()
    }

    override suspend fun loadAudioFile(path: String): AudioFile? =
        withContext(ioDispatcher) {
            assets.value.firstOrNull { asset ->
                asset.path == path
            }?.let {
                return@withContext getPlatform().loadAudioFile(it.path)
            }
            return@withContext null
        }

    override suspend fun deleteAudioFile(path: String) =
        withContext(ioDispatcher) {
           getPlatform().deleteAudioFile(path)
        }

    override suspend fun saveUserAudioFile(
        name: String,
        audioData: FloatArray,
        sampleRate: Int
    ) =
        withContext(ioDispatcher) {
            getPlatform().saveUserAudioFile(name, audioData, sampleRate)
            syncAssets()
        }
}
