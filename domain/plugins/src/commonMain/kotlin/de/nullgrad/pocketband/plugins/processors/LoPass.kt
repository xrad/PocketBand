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
import de.nullgrad.pocketband.plugins.model.addFrequencyParameter

class LoPass(id: Long, initializer: List<PresetParameter>) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "LoPass"
        const val KEY_CUTOFF = "cutoff"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "LoPass",
            kind = PlugInKind.AudioEffect,
            createPlugin = { id, initializer -> LoPass(id, initializer) },
        )
    }

    private val frequency = addFrequencyParameter(KEY_CUTOFF,
        1.0, 22000.0,initializer, 12000.0)

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val stk = LOCATOR.get<Stk>()

    private var handle = 0

    init {
        handle = stk.create(StkGen.FLT_LOPASS)
        rethink()
    }

    override fun dispose() {
        stk.destroy(handle)
        super.dispose()
    }

    override fun onParameterChange(parameter: PlugInParameter) {
        super.onParameterChange(parameter)
        when (parameter.key) {
            KEY_CUTOFF -> stk.setParameter(handle, StkGen.FX_PARAM_CUTOFF, frequency.effectiveValue)
        }
    }

    private fun rethink() {
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
