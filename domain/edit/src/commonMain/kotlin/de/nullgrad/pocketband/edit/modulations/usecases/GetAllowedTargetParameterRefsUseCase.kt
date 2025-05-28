package de.nullgrad.pocketband.edit.modulations.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.usecases.GetPluginUseCase
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.edit.uimodel.toParameterRef
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetAllowedTargetParameterRefsUseCase(
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val getPluginUseCase = GetPluginUseCase()

    suspend operator fun invoke(pluginId: Long) : List<ParameterRef>
        = withContext(dispatcher) {
        getPluginUseCase(pluginId).parameters
            .filter { !it.isOutput }
            .map { it.toParameterRef() }
    }
}