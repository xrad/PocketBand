package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.plugins.model.BoolPlugInParameter
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SetBoolParameterUseCase(
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val findPluginParameterUseCase = FindPluginParameterUseCase()

    suspend operator fun invoke(pluginId: Long, key: String, value: Boolean) = withContext(dispatcher) {
        findPluginParameterUseCase(pluginId, key)?.let {
            if (it is BoolPlugInParameter) {
                it.boolValue = value
                editPresetService.setModified()
            }
        }
    }
}