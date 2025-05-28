package de.nullgrad.pocketband.synth

import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInModulation
import de.nullgrad.pocketband.synth.service.SynthServiceImpl
import kotlinx.coroutines.flow.StateFlow

interface SynthService : Service {
    fun startOutput()
    fun stopOutput()

    fun keyDown(key: MidiKey, velocity: MidiVelocity)
    fun keyUp(key: MidiKey, velocity: MidiVelocity)

    fun setPlugins(list: List<PlugIn>)

    fun stopPlugin(plugin: PlugIn)
    fun startPlugin(plugin: PlugIn)

    fun setModulations(list: List<PlugInModulation>)

    val audioFilePlaying: StateFlow<AudioFile?>
    fun playAudioFile(audioFile: AudioFile)
    fun stopAudioFile()
}

object SynthModule {
    fun initialize() {
        SynthServiceImpl.registerService()
    }
}