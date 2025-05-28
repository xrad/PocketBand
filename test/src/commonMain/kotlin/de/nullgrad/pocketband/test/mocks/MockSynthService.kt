package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInModulation
import de.nullgrad.pocketband.synth.SynthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockSynthService : SynthService {
    companion object {
        fun registerService() {
            LOCATOR.register(SynthService::class) {
                MockSynthService()
            }
        }
    }

    override fun startOutput() {
    }

    override fun stopOutput() {
    }

    override fun keyDown(key: MidiKey, velocity: MidiVelocity) {
    }

    override fun keyUp(key: MidiKey, velocity: MidiVelocity) {
    }

    override fun setPlugins(list: List<PlugIn>) {
    }

    override fun stopPlugin(plugin: PlugIn) {
    }

    override fun startPlugin(plugin: PlugIn) {
    }

    override fun setModulations(list: List<PlugInModulation>) {
    }

    override val audioFilePlaying: StateFlow<AudioFile?>
        get() = MutableStateFlow(null)

    override fun playAudioFile(audioFile: AudioFile) {
    }

    override fun stopAudioFile() {
    }
}