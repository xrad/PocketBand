package de.nullgrad.pocketband.ui.plugins

import androidx.lifecycle.ViewModel
import de.nullgrad.pocketband.audioassets.AudioAssetsRepository
import de.nullgrad.pocketband.di.LOCATOR

class SamplerUIViewModel(
    private val audioAssets: AudioAssetsRepository = LOCATOR.get(),
) : ViewModel() {
    val assets = audioAssets.assets
}