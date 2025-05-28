package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.preset.EditPresetService
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DeleteProcessorUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val destroyProcessorSubUseCase = DestroyProcessorSubUseCase()

    suspend operator fun invoke(pluginId: Long) = withContext(dispatcher) {
        val plugins = editService.plugins.value.toMutableList()

        val index = plugins.indexOfFirst { it.id == pluginId }
        if (index >= 0) {
            val plugin = plugins.removeAt(index)
            destroyProcessorSubUseCase(plugin)
            editService.updatePluginList(plugins)
            editPresetService.setModified()
        }
    }
}