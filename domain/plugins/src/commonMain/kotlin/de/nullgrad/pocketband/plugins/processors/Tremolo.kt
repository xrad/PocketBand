package de.nullgrad.pocketband.plugins.processors

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addFrequencyParameter
import de.nullgrad.pocketband.plugins.model.addIntParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.modulators.ModLfo
import de.nullgrad.pocketband.plugins.toolbox.WaveTable
import de.nullgrad.pocketband.plugins.toolbox.WaveTableType

class Tremolo(id: Long, initializer: List<PresetParameter>) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "Tremolo"
        const val KEY_WAVEFORM = "waveform"
        const val KEY_FREQUENCY = "frequency"
        const val KEY_DEPTH = "depth"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Tremolo",
            kind = PlugInKind.AudioEffect,
            createPlugin = { id, initializer -> Tremolo(id, initializer) },
        )
    }

    private val waveform = addIntParameter(ModLfo.KEY_WAVEFORM, 0, WaveTableType.entries.size - 1, initializer, 0)
    private val frequency = addFrequencyParameter(KEY_FREQUENCY, 0.0, 120.0, initializer, 0.0)
    private val depth = addPercentParameter(KEY_DEPTH, initializer, 0.5)

    override val plugInDescriptor: PluginDescriptor = descriptor

    private var phaseIncrement = 0.0
    private var phase = 0.0
    private val waveTable = WaveTable(256)
    private var dirty = false

    override fun onParameterChange(parameter: PlugInParameter) {
        super.onParameterChange(parameter)
        if (parameter.id == waveform.id ||
            parameter.id == frequency.id) {
            dirty = true
        }
    }

    private fun rethink(playHead: PlayHead) {
        val type = WaveTableType.entries[waveform.intValue]
        waveTable.config(type, true, 1.0)
        phaseIncrement = frequency.effectiveValue / playHead.sampleRate
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        rethink(playHead)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        if (dirty) {
            dirty = false
            rethink(playHead)
        }

        var index = 0
        for (i in 0 until audioData.numFrames) {
            val mod = waveTable.getSample(phase)  // Use property directly
            val f = 1.0 - depth.effectiveValue * depth.effectiveValue * mod
            for (j in 0 until audioData.numChannels) {
                audioData[index] *= f.toFloat()  // Use multiplication assignment
                index++
            }
            phase += phaseIncrement
            phase %= 1.0  // Wrap phase using modulo
        }
    }


}
