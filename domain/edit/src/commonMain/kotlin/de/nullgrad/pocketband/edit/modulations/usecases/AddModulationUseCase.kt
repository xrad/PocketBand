package de.nullgrad.pocketband.edit.modulations.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.modulations.EditModulationsService
import de.nullgrad.pocketband.edit.plugins.usecases.FindPluginParameterUseCase
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AddModulationUseCase(
    private val editModulationsService: EditModulationsService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val findPluginParameterUseCase = FindPluginParameterUseCase()

    suspend operator fun invoke(
        source: ParameterRef, target: ParameterRef,
        amount: Double) = withContext(dispatcher) {
        val src = findPluginParameterUseCase(source.pluginId, source.key)
        val tgt = findPluginParameterUseCase(target.pluginId, target.key)
        if (src != null && tgt != null) {

            val mod = editModulationsService.createModulation(source = src, target = tgt, amount = amount)
            val modulations = editModulationsService.modulations.value.toMutableList().also { it.add(mod) }
            editModulationsService.updateModulationList(modulations)

            editPresetService.setModified()
        }
    }
}