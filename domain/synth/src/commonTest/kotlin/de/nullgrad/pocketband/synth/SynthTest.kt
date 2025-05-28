package de.nullgrad.pocketband.synth

import de.nullgrad.pocketband.audio.AudioOutput
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.EditModule
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.CreateProcessorUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetParameterUseCase
import de.nullgrad.pocketband.liveevents.LiveEventsModule
import de.nullgrad.pocketband.midi.MidiModule
import de.nullgrad.pocketband.plugins.PluginModule
import de.nullgrad.pocketband.test.mocks.MockAudioAssetsRepository
import de.nullgrad.pocketband.test.mocks.MockAudioOutput
import de.nullgrad.pocketband.test.mocks.MockPlugInAudioFx
import de.nullgrad.pocketband.test.mocks.MockPlugInInstrument
import de.nullgrad.pocketband.test.mocks.MockPlugInNoteFx
import de.nullgrad.pocketband.test.mocks.MockPluginRegistryProvider
import de.nullgrad.pocketband.test.mocks.MockPresetRepository
import de.nullgrad.pocketband.test.mocks.MockStk
import de.nullgrad.pocketband.test.util.runTestDi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SynthTest {

    @Before
    fun setup() {
        MockPluginRegistryProvider.registerService()
        MockAudioOutput.registerService()
        MockStk.registerService()
        MockPresetRepository.registerService()
        MockAudioAssetsRepository.registerService()
        MidiModule.initialize()
        LiveEventsModule.initialize()
        PluginModule.initialize()
        SynthModule.initialize()
        EditModule.initialize()
    }

    @After
    fun tearDown() {
        LOCATOR.clear()
    }

    /**
     * MockPlugInInstrument output value is incremented by the rate parameter
     * value in each sample. The tests sets rate to 0.001 and using a 100 byte
     * audio data block it verifies the output.
     */
    @Test
    fun singleInstrumentTest() = runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val setParameterUseCase = SetParameterUseCase()
        val audioOutput = LOCATOR.get<AudioOutput>() as MockAudioOutput
        val editService = LOCATOR.get<EditService>()
        val synthService = LOCATOR.get<SynthService>()

        audioOutput.setAudioDataSize(100, 1)

        createUseCase(MockPlugInInstrument.PLUGIN_TYPE)
        assertEquals(1, editService.plugins.value.size)

        val ratef = 0.001f
        val instrument = editService.plugins.value[0]
        setParameterUseCase(instrument.id,
            MockPlugInInstrument.KEY_PARAM_RATE, ratef.toDouble())

        synthService.startOutput()

        audioOutput.commandChannel.send(MockAudioOutput.Command.TriggerOutputCycle)

        synthService.stopOutput()

        val audioData = audioOutput.audioBlock.audioData
        for (i in 0 until audioData.numFrames) {
            for (j in 0 until audioData.numChannels) {
                assertEquals(ratef * i,
                    audioData[i * audioData.numChannels + j], 0.00001f)
            }
        }
    }

    /**
     * Verify simple instrument + audio fx chain
     */
    @Test
    fun instrumentAndAudioFxTest() = runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val setParameterUseCase = SetParameterUseCase()
        val audioOutput = LOCATOR.get<AudioOutput>() as MockAudioOutput
        val editService = LOCATOR.get<EditService>()
        val synthService = LOCATOR.get<SynthService>()

        audioOutput.setAudioDataSize(100, 1)

        createUseCase(MockPlugInInstrument.PLUGIN_TYPE)
        createUseCase(MockPlugInAudioFx.PLUGIN_TYPE)
        assertEquals(2, editService.plugins.value.size)

        val ratef = 0.001f
        val instrument = editService.plugins.value[0]
        setParameterUseCase(instrument.id,
            MockPlugInInstrument.KEY_PARAM_RATE, ratef.toDouble())

        val offsetf = 0.42f
        val audioFx = editService.plugins.value[1]
        setParameterUseCase(audioFx.id,
            MockPlugInAudioFx.KEY_PARAM_OFFSET, offsetf.toDouble())

        synthService.startOutput()

        audioOutput.commandChannel.send(MockAudioOutput.Command.TriggerOutputCycle)

        synthService.stopOutput()

        val audioData = audioOutput.audioBlock.audioData
        for (i in 0 until audioData.numFrames) {
            for (j in 0 until audioData.numChannels) {
                assertEquals(ratef * i + offsetf,
                    audioData[i * audioData.numChannels + j], 0.00001f)
            }
        }
    }
}