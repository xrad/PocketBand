package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.modulations.EditModulationsService
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.DestroyProcessorSubUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.FindPluginParameterUseCase
import de.nullgrad.pocketband.edit.preset.EditPresetService
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SetPresetUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val editModulationsService: EditModulationsService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val destroyProcessorSubUseCase = DestroyProcessorSubUseCase()
    private val findPluginParameterUseCase = FindPluginParameterUseCase()

    suspend operator fun invoke(preset: Preset) = withContext(dispatcher) {
        editService.plugins.value.let { plugins ->
            plugins.forEach { destroyProcessorSubUseCase(it) }
        }

        val plugins = preset.modules.map { presetModule ->
            editService.createPlugin(presetModule.id, presetModule.type, presetModule.parameters)
        }
        editService.updatePluginList(plugins)

        val modulations = preset.modulations.mapNotNull { presetModulation ->
            val sourceParameter = findPluginParameterUseCase(
                presetModulation.sourceModuleId, presetModulation.sourceParameterKey)
            val targetParameter = findPluginParameterUseCase(
                presetModulation.targetModuleId, presetModulation.targetParameterKey)
            if (sourceParameter != null && targetParameter != null) {
                editModulationsService.createModulation(sourceParameter, targetParameter,
                    presetModulation.amount.toDouble())
            }
            else {
                null
            }
        }
        editModulationsService.updateModulationList(modulations)
        editPresetService.setPresetName(preset.presetId.name)
        editPresetService.setPresetId(preset.presetId.id)
        editPresetService.setNotModified()
    }
}