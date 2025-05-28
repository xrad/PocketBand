package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.plugins.PlugInRegistryProvider

class MockPluginRegistryProvider : PlugInRegistryProvider {
    companion object {
        fun registerService() {
            LOCATOR.register(PlugInRegistryProvider::class) {
                MockPluginRegistryProvider()
            }
        }
    }

    override val registry = listOf(
        MockPlugInInstrument.descriptor,
        MockPlugInModulator.descriptor,
        MockPlugInAudioFx.descriptor,
        MockPlugInNoteFx.descriptor,
    )
}