package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.edit.uimodel.toParameterValue
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetPluginParameterValuesUseCase(
    val editService: EditService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    val getPluginUseCase = GetPluginUseCase(editService = editService)
    suspend operator fun invoke(pluginId: Long): List<ParameterValue> = withContext(dispatcher) {
        getPluginUseCase(pluginId).parameters
            .filter { !it.isOutput }
            .map { it.toParameterValue() }
    }
}
