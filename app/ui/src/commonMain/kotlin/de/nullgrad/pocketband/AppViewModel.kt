package de.nullgrad.pocketband

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.di.initServices
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.edit.preset.usecases.LoadPresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.SetPresetUseCase
import de.nullgrad.pocketband.engine.EngineController
import de.nullgrad.pocketband.presets.PresetRepository
import de.nullgrad.pocketband.presets.model.undefinedPreset
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    init {
        initServices()
    }

    private val presetRepository: PresetRepository = LOCATOR.get()
    private val engineController: EngineController = LOCATOR.get()
    private val editPresetService: EditPresetService = LOCATOR.get()

    private val loadPresetUseCase = LoadPresetUseCase()
    private val setPresetUseCase = SetPresetUseCase()

    private val presetId = editPresetService.id

    init {
        watchPresets()
    }

    private fun watchPresets() {
        viewModelScope.launch {
            presetRepository.presetIds.collect { presetIds ->
                // if no preset is currently loaded, load the first one
                if (presetId.value == undefinedPreset.presetId.id
                    && presetIds.isNotEmpty()) {
                    val preset = loadPresetUseCase(0)
                    setPresetUseCase(preset)
                }
            }
        }
    }

    fun onPause() {
        engineController.pauseEngine()
    }

    fun onResume() {
        engineController.resumeEngine()
    }

}