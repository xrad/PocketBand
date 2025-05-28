package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.modulations.EditModulationsService
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.plugins.model.PlugIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DestroyProcessorSubUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val editModulationsService: EditModulationsService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke(plugin: PlugIn) = withContext(dispatcher) {
        editModulationsService.modulations.value
            .toMutableList()
            .filter { it.source.owner.id != plugin.id && it.target.owner.id != plugin.id }
            .let { editModulationsService.updateModulationList(it) }
        editService.destroyPlugin(plugin)
    }
}