package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.presets.PresetRepository
import de.nullgrad.pocketband.di.LOCATOR

class GetNewModuleIdUseCase(
    private val presetRepository: PresetRepository = LOCATOR.get<PresetRepository>(),
) {
    suspend operator fun invoke() : Long {
        return presetRepository.getNewModuleId()
    }
}