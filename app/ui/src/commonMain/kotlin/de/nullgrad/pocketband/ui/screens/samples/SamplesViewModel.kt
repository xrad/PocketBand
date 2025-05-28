package de.nullgrad.pocketband.ui.screens.samples

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.audioassets.AudioAssetsRepository
import de.nullgrad.pocketband.audioassets.model.AudioAsset
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.engine.EngineController
import de.nullgrad.pocketband.synth.SynthService
import de.nullgrad.pocketband.ui.utils.scaleAndMergeChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class AudioAssetInfo(
    val asset: AudioAsset,
    val coroutineScope: CoroutineScope,
) {
    private val duration = mutableDoubleStateOf(0.0)

    private val numChannels = mutableIntStateOf(0)

    val thumbnail = mutableStateOf(doubleArrayOf())

    val txtLabel = asset.label
    val txtSource = if (asset.isBuiltIn) "built-in" else "user"
    val txtChannels get() = when (numChannels.intValue) {
        1 -> "Mono"
        2 -> "Stereo"
        else -> numChannels.toString()
    }
    val txtDuration get() = "%.1f s".format(duration.doubleValue)

    init {
        coroutineScope.launch {
            val audioAssets: AudioAssetsRepository = LOCATOR.get()
            audioAssets.loadAudioFile(asset.path)?.let {
                duration.doubleValue = it.duration
                numChannels.intValue = it.numChannels
                thumbnail.value = scaleAndMergeChannels(it.numSamples, it.soundData)
            }
        }
    }
}

class SamplesViewModel : ViewModel() {
    private val audioAssets: AudioAssetsRepository = LOCATOR.get()
    private val synthService: SynthService = LOCATOR.get()
    private val engineController: EngineController = LOCATOR.get()

    val engineMode = engineController.engineMode

    val assetInfos = audioAssets.assets
        .map { assets -> assets.map { AudioAssetInfo(it, viewModelScope) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // watch asset infos and audio file playing to identify currently playing asset
    val assetPlaying = synthService.audioFilePlaying
        .combine(audioAssets.assets) {
            audioFile, assetList -> assetList.firstOrNull {
                it.path == audioFile?.path
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun playAudioAsset(asset: AudioAsset) {
        viewModelScope.launch {
            audioAssets.loadAudioFile(asset.path)?.let { audioFile ->
                synthService.playAudioFile(audioFile)
            }
        }
    }

    fun stopAudioAsset() {
        viewModelScope.launch {
            synthService.stopAudioFile()
        }
    }

    fun deleteAudioAsset(asset: AudioAsset) {
        viewModelScope.launch {
            audioAssets.deleteAudioFile(asset.path)
        }
    }
}
