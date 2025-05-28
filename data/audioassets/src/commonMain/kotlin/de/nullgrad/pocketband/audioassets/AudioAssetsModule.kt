package de.nullgrad.pocketband.audioassets

import de.nullgrad.pocketband.audioassets.platform.getPlatform
import de.nullgrad.pocketband.audioassets.service.AudioAssetsRepositoryImpl

object AudioAssetsModule {
    fun initialize() {
        getPlatform().initialize()
        AudioAssetsRepositoryImpl.registerService()
    }
}