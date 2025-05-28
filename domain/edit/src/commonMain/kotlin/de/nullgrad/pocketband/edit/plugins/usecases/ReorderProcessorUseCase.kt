package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ReorderProcessorUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke(moveId: Long, beforeId: Long) = withContext(dispatcher) {
        val plugins = editService.plugins.value.toMutableList()

        val plugin = plugins.find { it.id == moveId }
        if (plugin == null) return@withContext

        plugins.removeIf { it.id == plugin.id }
        if (beforeId == ModuleRef.UNDEFINED.id) {
            plugins.add(plugin)
        } else {
            val beforeIndex = plugins.indexOfFirst { it.id == beforeId }
            if (beforeIndex < 0) return@withContext
            plugins.add(beforeIndex, plugin)
        }

        editService.updatePluginList(plugins)
        editPresetService.setModified()
    }
}