package de.nullgrad.pocketband.plugins

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.presets.model.PresetParameter

interface PlugInRegistry : Service {
    val noteEffects: List<PluginDescriptor>
    val noteFxLabels: List<String>
    val instruments: List<PluginDescriptor>
    val instrumentLabels: List<String>
    val audioEffects: List<PluginDescriptor>
    val effectLabels: List<String>
    val modulators: List<PluginDescriptor>
    val modulatorLabels: List<String>
    suspend fun createPluginInstance(moduleId: Long, moduleType: String, parameters: List<PresetParameter>) : PlugIn
    fun disposePluginInstance(plugIn: PlugIn)
}
