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
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.model.addTimingParameter

class PRCReverb(id: Long, initializer: List<PresetParameter>) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "PRCReverb"
        const val KEY_MIX = "mix"
        const val KEY_T60 = "t60"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "PRC Reverb",
            kind = PlugInKind.AudioEffect,
            createPlugin = { id, initializer -> PRCReverb(id, initializer) },
        )
    }

    private val mix = addPercentParameter(KEY_MIX, initializer, 0.7)
    private val size = addTimingParameter(KEY_T60, 0.1, 10000.0, initializer, 2.0)

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val stk = LOCATOR.get<Stk>()

    private var handle = 0

    init {
        handle = stk.create(StkGen.FX_PRC_REVERB)
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
            KEY_T60 -> stk.setParameter(handle, StkGen.FX_PARAM_T60, size.effectiveValue / 1000)
        }
    }

    private fun rethink() {
        stk.setParameter(handle, StkGen.FX_PARAM_MIX, mix.effectiveValue)
        stk.setParameter(handle, StkGen.FX_PARAM_T60, size.effectiveValue)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        stk.tickProcess(handle, audioData.raw, audioData.rawOffset, audioData.numFrames)
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        rethink()
    }
}
