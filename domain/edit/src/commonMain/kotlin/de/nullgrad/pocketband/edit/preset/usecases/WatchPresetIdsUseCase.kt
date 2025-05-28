package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.presets.PresetRepository
import de.nullgrad.pocketband.di.LOCATOR

class WatchPresetIdsUseCase(
    private val presetRepository: PresetRepository = LOCATOR.get<PresetRepository>(),
) {
    operator fun invoke()  = presetRepository.presetIds
}