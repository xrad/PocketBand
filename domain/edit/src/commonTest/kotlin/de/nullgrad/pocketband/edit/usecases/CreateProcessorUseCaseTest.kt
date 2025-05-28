package de.nullgrad.pocketband.edit.usecases

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.EditModule
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.CreateProcessorUseCase
import de.nullgrad.pocketband.plugins.PluginModule
import de.nullgrad.pocketband.test.mocks.MockAudioAssetsRepository
import de.nullgrad.pocketband.test.mocks.MockPlugInAudioFx
import de.nullgrad.pocketband.test.mocks.MockPlugInInstrument
import de.nullgrad.pocketband.test.mocks.MockPlugInModulator
import de.nullgrad.pocketband.test.mocks.MockPlugInNoteFx
import de.nullgrad.pocketband.test.mocks.MockPluginRegistryProvider
import de.nullgrad.pocketband.test.mocks.MockPresetRepository
import de.nullgrad.pocketband.test.mocks.MockSynthService
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreateProcessorUseCaseTests {

    @Before
    fun setup() {
        MockPluginRegistryProvider.registerService()
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
    fun createInstrumentUseCaseTest() = de.nullgrad.pocketband.test.util.runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val editService = LOCATOR.get<EditService>()
        var plugins = editService.plugins
        assertEquals(0, plugins.value.size)

        createUseCase(MockPlugInInstrument.PLUGIN_TYPE)
        plugins = editService.plugins
        assertEquals(1, plugins.value.size)
    }

    @Test
    fun createAudioEffectUseCaseTest() = de.nullgrad.pocketband.test.util.runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val editService = LOCATOR.get<EditService>()

        createUseCase(MockPlugInAudioFx.PLUGIN_TYPE)
        createUseCase(MockPlugInInstrument.PLUGIN_TYPE)

        val plugins = editService.plugins
        assertEquals(2, plugins.value.size)
        assertEquals(plugins.value[0].type, MockPlugInInstrument.PLUGIN_TYPE)
        assertEquals(plugins.value[1].type, MockPlugInAudioFx.PLUGIN_TYPE)
    }

    @Test
    fun createNoteEffectUseCaseTest() = de.nullgrad.pocketband.test.util.runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val editService = LOCATOR.get<EditService>()

        createUseCase(MockPlugInInstrument.PLUGIN_TYPE)
        createUseCase(MockPlugInAudioFx.PLUGIN_TYPE)
        createUseCase(MockPlugInNoteFx.PLUGIN_TYPE)

        val plugins = editService.plugins
        assertEquals(3, plugins.value.size)
        assertEquals(plugins.value[0].type, MockPlugInNoteFx.PLUGIN_TYPE)
        assertEquals(plugins.value[1].type, MockPlugInInstrument.PLUGIN_TYPE)
        assertEquals(plugins.value[2].type, MockPlugInAudioFx.PLUGIN_TYPE)
    }

    @Test
    fun createModulatorUseCaseTest() = de.nullgrad.pocketband.test.util.runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val editService = LOCATOR.get<EditService>()

        createUseCase(MockPlugInInstrument.PLUGIN_TYPE)
        createUseCase(MockPlugInAudioFx.PLUGIN_TYPE)
        createUseCase(MockPlugInModulator.PLUGIN_TYPE)
        createUseCase(MockPlugInNoteFx.PLUGIN_TYPE)

        val plugins = editService.plugins
        assertEquals(4, plugins.value.size)
        assertEquals(plugins.value[0].type, MockPlugInNoteFx.PLUGIN_TYPE)
        assertEquals(plugins.value[1].type, MockPlugInInstrument.PLUGIN_TYPE)
        assertEquals(plugins.value[2].type, MockPlugInAudioFx.PLUGIN_TYPE)
        assertEquals(plugins.value[3].type, MockPlugInModulator.PLUGIN_TYPE)
    }
}