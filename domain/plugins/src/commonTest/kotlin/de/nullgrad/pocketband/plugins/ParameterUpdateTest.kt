package de.nullgrad.pocketband.plugins

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.EditModule
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.CreateProcessorUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.GetPluginParameterValuesUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetMuteModuleUseCase
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.plugins.instruments.WaveOscillator
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.test.mocks.MockAudioAssetsRepository
import de.nullgrad.pocketband.test.mocks.MockPresetRepository
import de.nullgrad.pocketband.test.mocks.MockSynthService
import de.nullgrad.pocketband.test.util.joinTimeout
import de.nullgrad.pocketband.test.util.runTestDi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ParameterUpdateTest {

    @Before
    fun setup() {
        PluginModule.initialize()
        EditModule.initialize()
        MockSynthService.registerService()
        MockPresetRepository.registerService()
        MockAudioAssetsRepository.registerService()
    }

    @After
    fun tearDown() {
        LOCATOR.clear()
    }

    @Test
    fun parameterUpdateSentTest() = runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val editService = LOCATOR.get<EditService>()

        createUseCase(WaveOscillator.PLUGIN_TYPE)
        val plugins = editService.plugins
        assertEquals(1, plugins.value.size)

        val muteModuleUseCase = SetMuteModuleUseCase()
        val getPluginParameterValuesUseCase = GetPluginParameterValuesUseCase()

        val pluginId = plugins.value[0].id
        val parameterValues = getPluginParameterValuesUseCase(pluginId)
        val muteParam = parameterValues.find { it.parameter.key == PlugIn.KEY_MUTE }
        require(muteParam != null)

        var update : ParameterValue? = null
        val collector = launch {
            update = editService.parameterUpdates.first()
        }
        val valueToSet = !muteParam.asBool
        muteModuleUseCase(pluginId, valueToSet)
        val result = collector.joinTimeout()
        assertTrue(result)

        assertEquals(PlugIn.KEY_MUTE, update!!.parameter.key)
        assertEquals(valueToSet, update!!.asBool)
    }

}