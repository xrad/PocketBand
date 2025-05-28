package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.presets.PresetRepository

class ResetPresetUseCase(
    private val savePresetUseCase: SavePresetUseCase = SavePresetUseCase(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val presetRepository: PresetRepository = LOCATOR.get(),
) {
    suspend operator fun invoke() {
        val preset = presetRepository.createInitialPatch(editPresetService.id.value.toLong())
        savePresetUseCase(preset)
        editPresetService.setNotModified()
    }
}