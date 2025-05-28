package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.presets.PresetRepository
import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.di.LOCATOR

class SavePresetUseCase(
    private val presetRepository: PresetRepository = LOCATOR.get<PresetRepository>(),
) {
    suspend operator fun invoke(preset: Preset) {
        presetRepository.savePatch(preset)
    }
}