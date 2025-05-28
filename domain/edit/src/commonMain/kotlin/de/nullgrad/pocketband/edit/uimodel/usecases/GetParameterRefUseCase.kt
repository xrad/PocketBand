package de.nullgrad.pocketband.edit.uimodel.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.usecases.GetPluginUseCase
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.edit.uimodel.toParameterRef
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetParameterRefUseCase(
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    val getPluginUseCase = GetPluginUseCase()
    suspend operator fun invoke(
        pluginId: Long, key: String) : ParameterRef = withContext(dispatcher) {
        val plugin = getPluginUseCase(pluginId)
        val parameter = plugin.parameters.firstOrNull { it.key == key}
        require(parameter != null)
        parameter.toParameterRef()
    }
}