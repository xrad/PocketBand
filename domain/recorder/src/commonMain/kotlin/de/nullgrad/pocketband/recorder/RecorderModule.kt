package de.nullgrad.pocketband.recorder

import de.nullgrad.pocketband.recorder.service.RecordingServiceImpl

object RecorderModule {
    fun initialize() {
        RecordingServiceImpl.registerService()
    }
}