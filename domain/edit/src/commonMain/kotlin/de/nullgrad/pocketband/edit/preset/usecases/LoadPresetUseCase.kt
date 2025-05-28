package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.presets.PresetRepository
import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.di.IoDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class LoadPresetUseCase(
    private val presetRepository: PresetRepository = LOCATOR.get<PresetRepository>(),
    private val dispatcher: CoroutineContext = LOCATOR.get(IoDispatcher::class)
) {
    suspend operator fun invoke(id: Int) : Preset = withContext(dispatcher) {
        presetRepository.loadPreset(id)
    }
}