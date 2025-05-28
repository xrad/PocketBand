package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.presets.model.PresetId
import de.nullgrad.pocketband.presets.model.PresetModulation
import de.nullgrad.pocketband.presets.model.PresetModule
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.modulations.EditModulationsService
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.preset.EditPresetService
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetPresetUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val editModulationsService: EditModulationsService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke() : Preset = withContext(dispatcher) {
        Preset(
            presetId = PresetId(editPresetService.id.value,
                editPresetService.name.value),
            modules = editService.plugins.value.map { plugin ->
                PresetModule(
                    id = plugin.id,
                    type = plugin.type,
                    parameters = plugin.parameters
                        .mapNotNull { parameter ->
                            return@mapNotNull if (parameter.isOutput) {
                                null
                            } else PresetParameter(
                                key = parameter.key,
                                value = parameter.valueStr,
                            )
                        }
                )
            },
            modulations = editModulationsService.modulations.value.map { mod ->
                PresetModulation(
                    sourceModuleId = mod.source.owner.id,
                    sourceParameterKey = mod.source.key,
                    targetModuleId = mod.target.owner.id,
                    targetParameterKey = mod.target.key,
                    amount = mod.amount.toString()
                )
            }
        )
    }
}