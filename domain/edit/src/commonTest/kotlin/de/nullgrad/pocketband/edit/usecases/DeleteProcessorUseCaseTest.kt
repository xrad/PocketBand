package de.nullgrad.pocketband.edit.usecases

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.EditModule
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.CreateProcessorUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.DeleteProcessorUseCase
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

class DeleteProcessorUseCaseTests {

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
    fun deleteProcessorUseCaseTest() = de.nullgrad.pocketband.test.util.runTestDi {
        val createUseCase = CreateProcessorUseCase()
        val editService = LOCATOR.get<EditService>()

        createUseCase(MockPlugInInstrument.PLUGIN_TYPE)
        createUseCase(MockPlugInAudioFx.PLUGIN_TYPE)
        createUseCase(MockPlugInModulator.PLUGIN_TYPE)
        createUseCase(MockPlugInNoteFx.PLUGIN_TYPE)

        var plugins = editService.plugins
        assertEquals(4, plugins.value.size)
        assertEquals(plugins.value[0].type, MockPlugInNoteFx.PLUGIN_TYPE)
        assertEquals(plugins.value[1].type, MockPlugInInstrument.PLUGIN_TYPE)
        assertEquals(plugins.value[2].type, MockPlugInAudioFx.PLUGIN_TYPE)
        assertEquals(plugins.value[3].type, MockPlugInModulator.PLUGIN_TYPE)

        val deleteUseCase = DeleteProcessorUseCase()
        deleteUseCase(plugins.value[2].id)
        plugins = editService.plugins
        assertEquals(3, plugins.value.size)
        assertEquals(plugins.value[0].type, MockPlugInNoteFx.PLUGIN_TYPE)
        assertEquals(plugins.value[1].type, MockPlugInInstrument.PLUGIN_TYPE)
        assertEquals(plugins.value[2].type, MockPlugInModulator.PLUGIN_TYPE)

        // delete remaining
        deleteUseCase(plugins.value[0].id)
        deleteUseCase(plugins.value[0].id)
        deleteUseCase(plugins.value[0].id)
        plugins = editService.plugins
        assertEquals(0, plugins.value.size)
    }
}