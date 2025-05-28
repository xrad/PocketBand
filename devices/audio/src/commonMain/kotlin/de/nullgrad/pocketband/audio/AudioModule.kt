package de.nullgrad.pocketband.audio

import de.nullgrad.pocketband.audio.platform.getPlatform
import de.nullgrad.pocketband.audio.service.AudioInputImpl
import de.nullgrad.pocketband.audio.service.AudioOutputImpl

object AudioModule {
    fun initialize() {
        getPlatform().initialize()
        AudioOutputImpl.registerService()
        AudioInputImpl.registerService()
    }
}
