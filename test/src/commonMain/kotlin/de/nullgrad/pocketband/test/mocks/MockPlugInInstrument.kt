package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addBoolParameter
import de.nullgrad.pocketband.plugins.model.addDoubleParameter
import de.nullgrad.pocketband.plugins.model.addIntParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.model.addStringParameter
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.test.mocks.MockPlugInModulator.Companion.KEY_PARAM_RATE


open class MockPlugInInstrument(
    id: Long,
    initializer: List<PresetParameter>,
) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "mock_instrument"
        const val KEY_PARAM_RATE = "rate"
        const val KEY_PARAM_1 = "param1"
        const val KEY_PARAM_2 = "param2"
        const val KEY_PARAM_3 = "param3"
        const val KEY_PARAM_4 = "param4"
        const val KEY_PARAM_5 = "param5"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Mock Instrument",
            kind = PlugInKind.Instrument,
            createPlugin = { id, initializer ->
                MockPlugInInstrument(id, initializer)
            }
        )
    }

    val paramRate = addDoubleParameter(KEY_PARAM_RATE, 0.0, 1.0, initializer, 0.0)
    val param1 = addDoubleParameter(KEY_PARAM_1, 0.0, 10.0, initializer, 5.0)
    val param2 = addIntParameter(KEY_PARAM_2, 0, 10, initializer, 5)
    val param3 = addPercentParameter(KEY_PARAM_3, initializer, .5)
    val param4 = addStringParameter(KEY_PARAM_4, initializer, "Hello")
    val param5 = addBoolParameter(KEY_PARAM_5, initializer, false)

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

    var currentSample = 0.0f

    override fun start(playHead: PlayHead) {
        super.start(playHead)
    }

    override suspend fun process(
        playHead: PlayHead,
        audioData: AudioData,
        midiData: MidiData
    ) {
        for (i in 0 until audioData.numFrames) {
            for (j in 0 until audioData.numChannels) {
                audioData[i * audioData.numChannels + j] = currentSample
            }
            currentSample += paramRate.value.toFloat()
        }
    }
}

