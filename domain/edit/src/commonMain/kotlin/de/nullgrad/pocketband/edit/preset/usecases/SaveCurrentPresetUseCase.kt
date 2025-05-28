package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService

class SaveCurrentPresetUseCase(
    private val getPresetUseCase: GetPresetUseCase = GetPresetUseCase(),
    private val savePresetUseCase: SavePresetUseCase = SavePresetUseCase(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
) {
    suspend operator fun invoke() {
        val preset = getPresetUseCase()
        savePresetUseCase(preset)
        editPresetService.setNotModified()
    }
}