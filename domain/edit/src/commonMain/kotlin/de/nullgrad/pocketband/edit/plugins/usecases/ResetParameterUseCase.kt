package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.plugins.model.StringPlugInParameter
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ResetParameterUseCase(
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val findPluginParameterUseCase = FindPluginParameterUseCase()

    suspend operator fun invoke(pluginId: Long, key: String) = withContext(dispatcher) {
        findPluginParameterUseCase(pluginId, key)?.let {
            if (it is StringPlugInParameter) {
                if (it.valueStr != it.defaultValueStr) {
                    it.valueStr = it.defaultValueStr
                    editPresetService.setModified()
                }
            } else {
                if (it.value != it.defaultValue) {
                    it.value = it.defaultValue
                    editPresetService.setModified()
                }
            }
        }
    }
}