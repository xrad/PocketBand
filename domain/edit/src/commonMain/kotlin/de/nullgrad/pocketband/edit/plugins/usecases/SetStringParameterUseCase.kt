package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SetStringParameterUseCase(
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val findPluginParameterUseCase = FindPluginParameterUseCase()

    suspend operator fun invoke(pluginId: Long, key: String, value: String) = withContext(dispatcher) {
        findPluginParameterUseCase(pluginId, key)?.let {
            it.valueStr = value
            editPresetService.setModified()
        }
    }
}