package de.nullgrad.pocketband.edit.preset.usecases

class CopyPresetUseCase(
    private val loadPresetUseCase: LoadPresetUseCase = LoadPresetUseCase(),
    private val savePresetUseCase: SavePresetUseCase = SavePresetUseCase(),
    private val modifyPresetIdUseCase: ModifyPresetIdUseCase = ModifyPresetIdUseCase(),
) {

    suspend operator fun invoke(fromId: Int, toId: Int) {
        val preset = loadPresetUseCase(fromId)
        val adaptedPreset = modifyPresetIdUseCase(preset, toId, preset.presetId.name)
        savePresetUseCase(adaptedPreset)
    }
}