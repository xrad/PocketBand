package de.nullgrad.pocketband.plugins

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.plugins.model.PluginDescriptor

interface PlugInRegistryProvider : Service {
    val registry: List<PluginDescriptor>
}