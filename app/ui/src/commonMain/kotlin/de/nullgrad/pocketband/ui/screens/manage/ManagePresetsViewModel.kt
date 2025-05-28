package de.nullgrad.pocketband.ui.screens.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.edit.preset.usecases.CopyPresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.LoadPresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.RenamePresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.ResetPresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.SaveAsPresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.SaveCurrentPresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.SetPresetUseCase
import de.nullgrad.pocketband.edit.preset.usecases.WatchPresetIdsUseCase
import de.nullgrad.pocketband.presets.model.PresetId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManagePresetsViewModel : ViewModel() {
    private val editPresetService: EditPresetService = LOCATOR.get()

    private val loadPresetUseCase = LoadPresetUseCase()
    private val setPresetUseCase = SetPresetUseCase()
    private val copyPresetUseCase = CopyPresetUseCase()
    private val watchPresetIdsUseCase = WatchPresetIdsUseCase()
    private val saveAsPresetUseCase = SaveAsPresetUseCase()
    private val saveCurrentPresetUseCase = SaveCurrentPresetUseCase()
    private val renamePresetUseCase = RenamePresetUseCase()
    private val resetCurrentPresetUseCase = ResetPresetUseCase()

    private val _presetList = MutableStateFlow<List<PresetId>>(emptyList())
    val presetList = _presetList.asStateFlow()

    val presetId = editPresetService.id
    val presetName = editPresetService.name
    val isModified = editPresetService.isModified

    private val _canPaste = MutableStateFlow(false)
    val canPaste = _canPaste.asStateFlow()

    private var copyFromId: Int? = null

    init {
        watchPresets()
    }

    private fun watchPresets() {
        viewModelScope.launch {
            watchPresetIdsUseCase().collect { presetIds ->
                _presetList.value = presetIds
            }
        }
    }

    fun loadPreset(it: Int) {
        viewModelScope.launch {
            val preset = loadPresetUseCase(it)
            setPresetUseCase(preset)
        }
    }

    fun copyPreset() {
        viewModelScope.launch {
            copyFromId = presetId.value
            _canPaste.value = true
        }
    }

    fun pastePreset() {
        viewModelScope.launch {
            copyFromId?.let { fromId ->
                val toId = presetId.value
                copyPresetUseCase(fromId = fromId, toId = toId)
                loadPreset(toId)
                _canPaste.value = false
            }
        }
    }

    fun resetPreset() {
        viewModelScope.launch {
            resetCurrentPresetUseCase()
        }
    }

    fun savePreset() {
        viewModelScope.launch {
            saveCurrentPresetUseCase()
        }
    }

    fun renamePreset(name: String) {
        viewModelScope.launch {
            renamePresetUseCase(name)
        }
    }

    fun saveAsPreset(id: Int, name: String) {
        viewModelScope.launch {
            saveAsPresetUseCase(id, name)
        }
    }
}