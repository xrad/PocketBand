package de.nullgrad.pocketband.plugins.registry

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.plugins.PlugInRegistry
import de.nullgrad.pocketband.plugins.PlugInRegistryProvider
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.presets.model.PresetParameter

internal class PluginRegistryImpl private constructor() : PlugInRegistry {

    companion object {
        fun registerService() {
            LOCATOR.register(PlugInRegistry::class) {
                PluginRegistryImpl()
            }
        }
    }

    private val provider = LOCATOR.get<PlugInRegistryProvider>()

    private val registry = provider.registry

    override suspend fun createPluginInstance(moduleId: Long, moduleType: String, parameters: List<PresetParameter>) : PlugIn {
        val desc = registry.firstOrNull { it.type == moduleType }
        if (desc == null) {
            throw Exception("Unknown module $moduleType")
        }
        return desc.createPlugin(moduleId, parameters)
    }

    override fun disposePluginInstance(plugIn: PlugIn) {
        plugIn.dispose()
    }

    override val noteEffects = registry.filter { it.kind == PlugInKind.NoteEffect }
    override val noteFxLabels = noteEffects.map { it.label }

    override val instruments = registry.filter { it.kind == PlugInKind.Instrument }
    override val instrumentLabels = instruments.map { it.label }

    override val audioEffects = registry.filter { it.kind == PlugInKind.AudioEffect }
    override val effectLabels = audioEffects.map { it.label }

    override val modulators = registry.filter { it.kind == PlugInKind.Modulator }
    override val modulatorLabels = modulators.map { it.label }

}


