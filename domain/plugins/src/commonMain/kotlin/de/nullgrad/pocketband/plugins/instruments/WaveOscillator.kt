package de.nullgrad.pocketband.plugins.instruments

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.liveevents.PluginUpdateProvider
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.midi.model.frequency
import de.nullgrad.pocketband.midi.model.maxMidiVelocity
import de.nullgrad.pocketband.plugins.model.IntPlugInParameter
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.SemitonesPlugInParameter
import de.nullgrad.pocketband.plugins.model.addIntParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.model.addSemitoneParameter
import de.nullgrad.pocketband.plugins.model.addVolumeParameter
import de.nullgrad.pocketband.plugins.nullPlugin
import de.nullgrad.pocketband.plugins.polysynth.PolySynth
import de.nullgrad.pocketband.plugins.polysynth.Voice
import de.nullgrad.pocketband.plugins.polysynth.VoiceManager
import de.nullgrad.pocketband.plugins.polysynth.VoiceState
import de.nullgrad.pocketband.plugins.toolbox.Adsr
import de.nullgrad.pocketband.plugins.toolbox.AdsrVoice
import de.nullgrad.pocketband.plugins.toolbox.WaveTable
import de.nullgrad.pocketband.plugins.toolbox.WaveTableType
import de.nullgrad.pocketband.presets.model.PresetParameter
import kotlin.math.pow

@Immutable
data class WaveOscillatorLiveState(
    override val pluginId: Long = nullPlugin.id,
    val numUsedVoices: Int = 0,
) : PluginUpdate

class WaveOscillatorVoice(private val synth: WaveOscillator) : Voice() {

    private val phase = DoubleArray(WaveOscillator.MAX_UNISONS) { 0.0 }
    private val phaseIncrement = DoubleArray(WaveOscillator.MAX_UNISONS) { 0.0 }
    private val envelope: AdsrVoice = AdsrVoice(synth.envelope)

    override fun start(playHead: PlayHead, key: MidiKey, velocity: MidiVelocity) {
        // Reset phase only for new notes (not restarts or duplicates)
        if (state != VoiceState.Playing) {
            phase.fill(0.0)
        }
        envelope.start(playHead)
        super.start(playHead, key, velocity)
        rethinkDetune(playHead)
    }

    override fun release(playHead: PlayHead) {
        envelope.release(playHead)
        super.release(playHead)
    }

    private val numUnisons: Int
        get() = synth.unison.intValue

    private var unisonWeight = 1.0

    internal fun rethinkDetune(playHead: PlayHead) {
        val detuneRange = synth.detune.effectiveValue / 12.0
        val detune = 2.0.pow(detuneRange)
        val frequency = key.frequency * detune
        for (u in 0 until numUnisons) {
            val unisonPosition = u - numUnisons / 2.0
            val unisonDetune = 2.0.pow(unisonPosition * synth.unisonWidth.effectiveValue / 12.0)
            val unisonFrequency = frequency * unisonDetune
            phaseIncrement[u] = unisonFrequency / playHead.sampleRate
        }
        unisonWeight = 1.0 / numUnisons
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData) {
        val velocityGain = velocity.toDouble() / maxMidiVelocity
        var index = 0
        val gain = velocityGain * synth.volume.effectiveValue * envelope.level * unisonWeight
        for (i in 0 until audioData.numFrames) {
            for (u in 0 until numUnisons) {
                val sample = synth.waveTable.getSample(phase[u]) * gain
                for (j in 0 until audioData.numChannels) {
                    audioData[index + j] += sample.toFloat()
                }
                phase[u] += phaseIncrement[u]
                if (phase[u] > 1.0) {
                    phase[u] -= 1.0
                }
            }
            index += audioData.numChannels
            if (envelope.increment(playHead, state) == VoiceState.Idle) {
                stop(playHead)
                break
            }
        }
    }
}

class WaveOscillator(id: Long, initializer: List<PresetParameter>)
    : PolySynth<WaveOscillatorVoice>(id), PluginUpdateProvider {

    companion object {
        const val PLUGIN_TYPE = "WaveOscillator"
        const val MAX_UNISONS = 5

        const val KEY_DETUNE = "detune"
        const val KEY_VOLUME = "volume"
        const val KEY_WAVEFORM = "waveform"
        const val KEY_UNISON = "unison"
        const val KEY_UNISON_WIDTH = "unison_width"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Wave Oscillator",
            kind = PlugInKind.Instrument,
            createPlugin = { id: Long, initializer: List<PresetParameter> -> WaveOscillator(id, initializer) }
        )
    }

    override val plugInDescriptor: PluginDescriptor
        get() = descriptor

    internal val envelope: Adsr = Adsr(this, initializer)

    private var _needRethinkVoices = false
    private var _needRethinkWT = false

    internal val waveTable = WaveTable(256)

    private val waveform: IntPlugInParameter
    internal val detune: SemitonesPlugInParameter
    internal val volume: PlugInParameter
    internal val unison: IntPlugInParameter
    internal val unisonWidth: PlugInParameter

    private var lastNumUsedVoices = 0

    init {
        val voiceManager = VoiceManager(8) { WaveOscillatorVoice(this) }
        initVoiceManager(voiceManager)
        detune = addSemitoneParameter(KEY_DETUNE, -24.0, 24.0, false, initializer, 0.0)
        volume = addVolumeParameter(KEY_VOLUME, 0.0, 1.0, initializer, 0.7)
        unison = addIntParameter(KEY_UNISON, 1, MAX_UNISONS, initializer, 1)
        unisonWidth = addPercentParameter(KEY_UNISON_WIDTH, initializer, 0.3)
        waveform = addIntParameter(KEY_WAVEFORM, 0, WaveTableType.entries.size - 1, initializer, 0)
    }

    override fun onParameterChange(parameter: PlugInParameter) {
        super.onParameterChange(parameter)
        _needRethinkVoices = parameter.id != waveform.id
        _needRethinkWT = parameter.id == waveform.id
    }

    private fun rethinkWT() {
        val type = WaveTableType.entries[waveform.intValue]
        when (type) {
            WaveTableType.Sine -> waveTable.sine()
            WaveTableType.Triangle -> waveTable.triangle()
            WaveTableType.Saw -> waveTable.saw()
        }
    }

    private fun rethinkVoices(playHead: PlayHead) {
        voiceManager.forAllVoices { voice ->
            if (voice.isActive) {
                voice.rethinkDetune(playHead)
            }
        }
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        lastNumUsedVoices = 0
        rethinkWT()
        _needRethinkWT = false
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        if (_needRethinkVoices) {
            _needRethinkVoices = false
            rethinkVoices(playHead)
        }
        if (_needRethinkWT) {
            _needRethinkWT = false
            rethinkWT()
        }
        super.process(playHead, audioData, midiData)
    }

    override fun getPluginUpdate(): PluginUpdate? {
        if (lastNumUsedVoices == numUsedVoices) {
            return null
        }
        return WaveOscillatorLiveState(id, numUsedVoices).also {
            lastNumUsedVoices = numUsedVoices
        }
    }
}
