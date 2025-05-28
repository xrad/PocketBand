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

class FreeVerb(id: Long, initializer: List<PresetParameter>) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "FreeVerb"
        const val KEY_MIX = "mix"
        const val KEY_SIZE = "size"
        const val KEY_WIDTH = "width"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "FreeVerb",
            kind = PlugInKind.AudioEffect,
            createPlugin = { id, initializer -> FreeVerb(id, initializer) },
        )
    }

    private val mix = addPercentParameter(KEY_MIX, initializer, 0.7)
    private val size = addPercentParameter(KEY_SIZE, initializer, 0.5)
    private val width = addPercentParameter(KEY_WIDTH, initializer, 1.0)

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val stk = LOCATOR.get<Stk>()

    private var handle = 0

    init {
        handle = stk.create(StkGen.FX_FREEVERB)
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
            KEY_SIZE -> stk.setParameter(handle, StkGen.FX_PARAM_SIZE, size.effectiveValue)
            KEY_WIDTH -> stk.setParameter(handle, StkGen.FX_PARAM_WIDTH, width.effectiveValue)
        }
    }

    private fun rethink() {
        stk.setParameter(handle, StkGen.FX_PARAM_MIX, mix.effectiveValue)
        stk.setParameter(handle, StkGen.FX_PARAM_SIZE, size.effectiveValue)
        stk.setParameter(handle, StkGen.FX_PARAM_WIDTH, width.effectiveValue)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        stk.tickProcess(handle, audioData.raw, audioData.rawOffset, audioData.numFrames)
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        rethink()
    }
}
