package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.preset.EditPresetService
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ReplaceProcessorUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val destroyProcessorSubUseCase = DestroyProcessorSubUseCase()
    private val getNewModuleIdUseCase = GetNewModuleIdUseCase()

    suspend operator fun invoke(moduleType: String, replaceId: Long) = withContext(dispatcher) {
        val pluginList = editService.plugins.value.toMutableList()
        val oldPlugin = pluginList.find { it.id == replaceId }
        if (oldPlugin == null) return@withContext
        val position = pluginList.indexOf(oldPlugin)

        val moduleId = getNewModuleIdUseCase()
        // TODO can we replace emptyList with more reasonable initializers?
        val plugin = editService.createPlugin(moduleId, moduleType, emptyList())

        pluginList[position] = plugin
        editService.updatePluginList(pluginList)

        destroyProcessorSubUseCase(oldPlugin)
        editPresetService.setModified()
    }
}