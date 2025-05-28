package de.nullgrad.pocketband.plugins.processors

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.stk.Stk
import de.nullgrad.pocketband.stk.StkGen
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addDoubleParameter
import de.nullgrad.pocketband.plugins.model.addFrequencyParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter

class Chorus(id: Long, initializer: List<PresetParameter>) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "Chorus"
        const val KEY_MIX = "mix"
        const val KEY_DEPTH = "depth"
        const val KEY_FREQUENCY = "frequency"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Chorus",
            kind = PlugInKind.AudioEffect,
            createPlugin = { id, initializer -> Chorus(id, initializer) },
        )
    }

    private val mix = addPercentParameter(KEY_MIX, initializer, 0.7)
    private val depth = addDoubleParameter(KEY_DEPTH, 0.0, 0.2, initializer, 0.5)
    private val frequency = addFrequencyParameter(KEY_FREQUENCY, 0.0, 10.0,
        initializer, 0.0)

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val stk = LOCATOR.get<Stk>()

    private var handle = 0

    init {
        handle = stk.create(StkGen.FX_CHORUS)
        rethink()
    }

    override fun dispose() {
        stk.destroy(handle)
        super.dispose()
    }

    override fun onParameterChange(parameter: PlugInParameter) {
        super.onParameterChange(parameter)
        when (parameter.key) {
            KEY_MIX -> stk.setParameter(handle, StkGen.FX_PARAM_MIX, mix.effectiveValue)
            KEY_DEPTH -> stk.setParameter(handle, StkGen.FX_PARAM_DEPTH, depth.effectiveValue)
            KEY_FREQUENCY -> stk.setParameter(handle, StkGen.FX_PARAM_FREQUENCY, frequency.effectiveValue)
        }
    }

    private fun rethink() {
        stk.setParameter(handle, StkGen.FX_PARAM_MIX, mix.effectiveValue)
        stk.setParameter(handle, StkGen.FX_PARAM_DEPTH, depth.effectiveValue)
        stk.setParameter(handle, StkGen.FX_PARAM_FREQUENCY, frequency.effectiveValue)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        stk.tickProcess(handle, audioData.raw, audioData.rawOffset, audioData.numFrames)
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        rethink()
    }
}
