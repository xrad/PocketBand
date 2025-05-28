package de.nullgrad.pocketband.plugins

import de.nullgrad.pocketband.plugins.registry.PlugInRegistryProviderImpl
import de.nullgrad.pocketband.plugins.registry.PluginRegistryImpl

object PluginModule {
    fun initialize() {
        PlugInRegistryProviderImpl.registerService()
        PluginRegistryImpl.registerService()
    }
}