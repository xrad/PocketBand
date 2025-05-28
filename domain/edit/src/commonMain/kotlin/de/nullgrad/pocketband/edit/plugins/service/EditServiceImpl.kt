package de.nullgrad.pocketband.edit.plugins.service

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.edit.uimodel.toParameterValue
import de.nullgrad.pocketband.plugins.PlugInRegistry
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.synth.SynthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


internal class EditServiceImpl : EditService {
    companion object {
        fun registerService() {
            LOCATOR.register(EditService::class) {
                EditServiceImpl()
            }
        }
    }

    private val _plugins = MutableStateFlow(emptyList<PlugIn>())
    override val plugins = _plugins.asStateFlow()

    private val _parameterUpdates = MutableSharedFlow<ParameterValue>()
    override val parameterUpdates = _parameterUpdates.asSharedFlow()

    private val pluginRegistry = LOCATOR.get<PlugInRegistry>()
    private val synthService = LOCATOR.get<SynthService>()

    private val defaultDispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
    private val coroutineScope = CoroutineScope(defaultDispatcher)

    override fun updatePluginList(list: List<PlugIn>) {
        synthService.setPlugins(list)
        _plugins.value = list
    }

    override fun destroyPlugin(plugin: PlugIn) {
        synthService.stopPlugin(plugin)
        plugin.removeParameterChangeListener(::onParameterChange)
        pluginRegistry.disposePluginInstance(plugin)
    }

    override suspend fun createPlugin(
        moduleId: Long,
        moduleType: String,
        parameters: List<PresetParameter>
    ): PlugIn {
        val plugin = pluginRegistry.createPluginInstance(
            moduleId,
            moduleType,
            parameters
        )
        plugin.addParameterChangeListener(::onParameterChange)
        synthService.startPlugin(plugin)
        return plugin
    }

    private fun onParameterChange(parameter: PlugInParameter) {
        coroutineScope.launch {
            _parameterUpdates.emit(parameter.toParameterValue())
        }
    }
}