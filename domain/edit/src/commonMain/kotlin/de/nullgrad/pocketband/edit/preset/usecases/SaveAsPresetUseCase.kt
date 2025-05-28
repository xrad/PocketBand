package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService

class SaveAsPresetUseCase(
    private val getPresetUseCase: GetPresetUseCase = GetPresetUseCase(),
    private val savePresetUseCase: SavePresetUseCase = SavePresetUseCase(),
    private val modifyPresetIdUseCase: ModifyPresetIdUseCase = ModifyPresetIdUseCase(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
) {
    suspend operator fun invoke(id: Int, name: String) {
        val preset = getPresetUseCase()
        val adaptedPreset = modifyPresetIdUseCase(preset, id, name)
        savePresetUseCase(adaptedPreset)
        editPresetService.setNotModified()
    }
}