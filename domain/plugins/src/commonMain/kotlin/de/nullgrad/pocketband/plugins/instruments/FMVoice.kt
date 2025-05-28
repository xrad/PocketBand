package de.nullgrad.pocketband.plugins.instruments

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.stk.Stk
import de.nullgrad.pocketband.stk.StkGen
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.midi.model.frequency
import de.nullgrad.pocketband.midi.model.maxMidiVelocity
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addIntParameter
import de.nullgrad.pocketband.plugins.polysynth.PolySynth
import de.nullgrad.pocketband.plugins.polysynth.Voice
import de.nullgrad.pocketband.plugins.polysynth.VoiceManager

class FMVoiceVoice(val synth: FMVoice) : Voice() {

    private val stk = LOCATOR.get<Stk>()
    private val handle = stk.create(StkGen.INST_FM_VOICE)

    override fun dispose() {
        super.dispose()
        stk.destroy(handle)
    }

    override fun start(playHead: PlayHead, key: MidiKey, velocity: MidiVelocity) {
        super.start(playHead, key, velocity)
        stk.noteOn(handle, key.frequency.toFloat(), velocity.toFloat() / maxMidiVelocity)
    }

    override fun release(playHead: PlayHead) {
        super.release(playHead)
        stk.noteOff(handle, 0f)
        // no envelope yet
        stop(playHead)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData) {
        stk.tickInstrument(handle, audioData.raw, audioData.rawOffset, audioData.numFrames)
    }

    fun onParameterChange(key: String) {
        when (key) {
            FMVoice.KEY_VOWEL -> stk.setParameter(handle, StkGen.INST_PARAM_CONT4, synth.vowel.effectiveValue)
            FMVoice.KEY_FORMANT -> stk.setParameter(handle, StkGen.INST_PARAM_CONT2, synth.formant.effectiveValue)
            FMVoice.KEY_VIBRATO_RATE -> stk.setParameter(handle, StkGen.INST_PARAM_CONT11, synth.vibratoRate.effectiveValue)
            FMVoice.KEY_VIBRATO_AMOUNT -> stk.setParameter(handle, StkGen.INST_PARAM_CONT1, synth.vibratoAmount.effectiveValue)
        }
    }
}

class FMVoice(id: Long, initializer: List<PresetParameter>)
    : PolySynth<FMVoiceVoice>(id) {

    companion object {
        const val PLUGIN_TYPE = "FMVoice"
        const val KEY_VOWEL = "vowel"
        const val KEY_FORMANT = "formant"
        const val KEY_VIBRATO_RATE = "vibratorate"
        const val KEY_VIBRATO_AMOUNT = "vibratoamount"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "FM Voice",
            kind = PlugInKind.Instrument,
            createPlugin = { id: Long, initializer: List<PresetParameter> -> FMVoice(id, initializer) }
        )
    }

    override val plugInDescriptor: PluginDescriptor
        get() = descriptor

    internal val vowel = addIntParameter(KEY_VOWEL, 0, 127, initializer, 0)
    internal val formant = addIntParameter(KEY_FORMANT, 0, 127, initializer, 0)
    internal val vibratoRate = addIntParameter(KEY_VIBRATO_RATE, 0, 127, initializer, 0)
    internal val vibratoAmount = addIntParameter(KEY_VIBRATO_AMOUNT, 0, 127, initializer, 0)

    var needParameterUpdate: String? = null

    init {
        val voiceManager = VoiceManager(8) { FMVoiceVoice(this) }
        initVoiceManager(voiceManager)
        voiceManager.forAllVoices {
            it.onParameterChange(vowel.key)
            it.onParameterChange(formant.key)
            it.onParameterChange(vibratoRate.key)
            it.onParameterChange(vibratoAmount.key)
        }
    }

    override fun onParameterChange(parameter: PlugInParameter) {
        super.onParameterChange(parameter)
        needParameterUpdate = parameter.key
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        needParameterUpdate?.let { key ->
            needParameterUpdate = null
            voiceManager.forAllVoices { it.onParameterChange(key) }
        }
        super.process(playHead, audioData, midiData)
    }
}
