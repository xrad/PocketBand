package de.nullgrad.pocketband.plugins.modulators

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.liveevents.PluginUpdateProvider
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiEvent
import de.nullgrad.pocketband.midi.model.MidiKeyDown
import de.nullgrad.pocketband.midi.model.midiTimeLive
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addBoolParameter
import de.nullgrad.pocketband.plugins.model.addDoubleParameter
import de.nullgrad.pocketband.plugins.model.addFrequencyParameter
import de.nullgrad.pocketband.plugins.model.addIntParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.nullPlugin
import de.nullgrad.pocketband.plugins.toolbox.WaveTable
import de.nullgrad.pocketband.plugins.toolbox.WaveTableType
import de.nullgrad.pocketband.presets.model.PresetParameter

@Immutable
data class LfoLiveState(
    override val pluginId: Long = nullPlugin.id,
    val phase: Double = 0.0,
    val value: Double = 0.0,
) : PluginUpdate

class ModLfo(id: Long, initializer: List<PresetParameter>) : PlugIn(id), PluginUpdateProvider {
    companion object {
        const val PLUGIN_TYPE = "LFO"
        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "LFO",
            kind = PlugInKind.Modulator,
            createPlugin = { id, initializer -> ModLfo(id, initializer) },
        )
        const val KEY_WAVEFORM = "waveform"
        const val KEY_FREQUENCY = "frequency"
        const val KEY_DEPTH = "depth"
        const val KEY_OUTPUT = "output"
        const val KEY_BIPOLAR = "bipolar"
        const val KEY_FREERUNNING = "freerunning"
    }

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val waveform =
        addIntParameter(KEY_WAVEFORM, 0, WaveTableType.entries.size - 1, initializer, 0)
    private val frequency =
        addFrequencyParameter(KEY_FREQUENCY, 0.0, 50.0, initializer, 0.0)
    private val depth =
        addPercentParameter(KEY_DEPTH, initializer, 0.5)
    private val bipolar =
        addBoolParameter(KEY_BIPOLAR, initializer, false)
    private val output =
        addDoubleParameter(KEY_OUTPUT, -1.0, 1.0, initializer, 0.0, isOutput = true)
    private val freeRunning =
        addBoolParameter(KEY_FREERUNNING, initializer, false)

    private var _phaseIncrement = 0.0
    private var _phase = 0.0
    private val waveTable = WaveTable(256)
    private var _dirty = true

    override fun onParameterChange(parameter: PlugInParameter) {
        super.onParameterChange(parameter)
        if (parameter.id == waveform.id ||
            parameter.id == frequency.id ||
            parameter.id == bipolar.id
        ) {
            _dirty = true
        }
    }

    private fun rethink(playHead: PlayHead) {
        val type = WaveTableType.entries[waveform.intValue]
        waveTable.config(type, bipolar.boolValue, 1.0)
        _phaseIncrement = frequency.effectiveValue / playHead.sampleRate
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        rethink(playHead)
        _dirty = false
        _phase = 0.0
        output.value = getMod(_phase)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        if (_dirty) {
            _dirty = false
            rethink(playHead)
        }
        if (!freeRunning.boolValue) {
            for (event in getMidiEventsForBlock(playHead, audioData, midiData)) {
                if (event is MidiKeyDown) {
                    _phase = 0.0
                }
            }
        }
        output.value = getMod(_phase)
        _phase += _phaseIncrement * audioData.numFrames
        while (_phase >= 1.0) {
            _phase -= 1.0
        }
    }

    override fun getPluginUpdate(): PluginUpdate {
        return LfoLiveState(id, _phase, output.value)
    }

    private fun getMidiEventsForBlock(
        playHead: PlayHead,
        audioData: AudioData,
        midiData: MidiData
    ): Sequence<MidiEvent> {
        val sampleTime = playHead.samplePos
        val audioWindowEnd = sampleTime + audioData.numFrames
        return sequence {
            for (event in midiData.events) {
                val eventAudioTime = event.timestamp
                if (eventAudioTime != midiTimeLive && eventAudioTime > audioWindowEnd) {
                    break
                }
                yield(event)
            }
        }
    }

    private fun getMod(phase: Double): Double {
        return waveTable.getSample(phase) * depth.effectiveValue
    }

}