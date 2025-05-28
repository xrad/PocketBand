package de.nullgrad.pocketband.edit.plugins

import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface EditService : Service {
    val plugins : StateFlow<List<PlugIn>>
    fun updatePluginList(list: List<PlugIn>)
    suspend fun createPlugin(moduleId: Long, moduleType: String, parameters: List<PresetParameter>) : PlugIn
    fun destroyPlugin(plugin: PlugIn)

    val parameterUpdates: SharedFlow<ParameterValue>
}