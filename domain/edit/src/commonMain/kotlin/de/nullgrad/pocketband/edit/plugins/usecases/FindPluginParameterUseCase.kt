package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class FindPluginParameterUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke(pluginId: Long, key: String): PlugInParameter? = withContext(dispatcher) {
        editService.plugins.value.find { it.id == pluginId }?.let { p ->
            p.parameters.find { it.key == key }
        }
    }
}
