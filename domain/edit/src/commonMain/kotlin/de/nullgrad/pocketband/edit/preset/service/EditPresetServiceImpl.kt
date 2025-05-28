package de.nullgrad.pocketband.edit.preset.service

import de.nullgrad.pocketband.presets.model.undefinedPreset
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class EditPresetServiceImpl : EditPresetService {
    companion object {
        fun registerService() {
            LOCATOR.register(EditPresetService::class) {
                EditPresetServiceImpl()
            }
        }
    }

    private val _id = MutableStateFlow(undefinedPreset.presetId.id)
    override val id = _id.asStateFlow()

    private val _name = MutableStateFlow(undefinedPreset.presetId.name)
    override val name = _name.asStateFlow()

    private val _isModified = MutableStateFlow(false)
    override val isModified = _isModified.asStateFlow()

    override fun setNotModified() {
        _isModified.value = false
    }

    override fun setModified() {
        _isModified.value = true
    }

    override fun setPresetId(id: Int) {
        _id.value = id
    }

    override fun setPresetName(name: String) {
        _name.value = name
    }
}