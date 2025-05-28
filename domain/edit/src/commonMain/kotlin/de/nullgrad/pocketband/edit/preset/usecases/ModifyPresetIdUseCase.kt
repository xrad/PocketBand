package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.edit.plugins.usecases.GetNewModuleIdUseCase

class ModifyPresetIdUseCase(
    private val getNewModuleIdUseCase: GetNewModuleIdUseCase = GetNewModuleIdUseCase(),
) {
    suspend operator fun invoke(preset: Preset, newId: Int, newName: String) : Preset {
        if (newId == preset.presetId.id) {
            if (newName == preset.presetId.name) {
                return preset
            }
            return preset.copy(presetId = preset.presetId.copy(name = newName))
        }
        var newPresetModulations = preset.modulations
        val newPresetId = preset.presetId.copy(id = newId, name = newName)
        val newPresetModules = preset.modules.map {
            val oldModuleId = it.id
            val newModuleId = getNewModuleIdUseCase()
            newPresetModulations = newPresetModulations.map { modulation ->
                modulation.copy(
                    sourceModuleId = if (modulation.sourceModuleId == oldModuleId) newModuleId else modulation.sourceModuleId,
                    targetModuleId = if (modulation.targetModuleId == oldModuleId) newModuleId else modulation.targetModuleId
                )
            }
            it.copy(id = newModuleId)
        }
        return preset.copy(
            presetId = newPresetId,
            modules = newPresetModules,
            modulations = newPresetModulations
        )
    }
}