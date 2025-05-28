package de.nullgrad.pocketband.audio

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.Service

interface AudioOutput : Service {
    val sampleRate: Int
    fun start()
    suspend fun processOutput(fillBuffer: suspend (AudioData) -> Unit)
    fun stop()
}

