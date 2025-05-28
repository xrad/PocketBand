package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.plugins.model.PlugIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetPluginUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke(pluginId: Long): PlugIn = withContext(dispatcher) {
        val plugin = editService.plugins.value.firstOrNull { it.id == pluginId }
        require(plugin != null)
        plugin
    }

}
