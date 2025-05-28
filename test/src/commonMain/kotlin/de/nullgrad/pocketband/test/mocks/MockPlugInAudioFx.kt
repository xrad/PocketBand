package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addDoubleParameter
import de.nullgrad.pocketband.presets.model.PresetParameter


open class MockPlugInAudioFx(
    id: Long,
    initializer: List<PresetParameter>,
) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "mock_audiofx"
        const val KEY_PARAM_OFFSET = "offset"
        const val KEY_PARAM_OUTPUT = "output"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Mock Audio Fx",
            kind = PlugInKind.AudioEffect,
            createPlugin = { id, initializer ->
                MockPlugInAudioFx(id, initializer)
            }
        )
    }

    val offset = addDoubleParameter(KEY_PARAM_OFFSET, 0.0, 1.0, initializer, 0.0)
    val output = addDoubleParameter(KEY_PARAM_OUTPUT, 0.0, 10.0, initializer, 5.0)

    var listenerCalledCounter = 0
    var lastParameterUpdate: PlugInParameter? = null

    init {
        addParameterChangeListener { param ->
            listenerCalledCounter++
            lastParameterUpdate = param
        }
    }

    override val plugInDescriptor: PluginDescriptor
        get() = descriptor

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        output.value = 0.0
    }

    override suspend fun process(
        playHead: PlayHead,
        audioData: AudioData,
        midiData: MidiData
    ) {
        val o = offset.effectiveValue.toFloat()
        for (i in 0 until audioData.numFrames) {
            for (j in 0 until audioData.numChannels) {
                audioData[i * audioData.numChannels + j] += o
            }
        }
    }
}

