package de.nullgrad.pocketband.edit

import de.nullgrad.pocketband.edit.modulations.service.EditModulationsServiceImpl
import de.nullgrad.pocketband.edit.plugins.service.EditServiceImpl
import de.nullgrad.pocketband.edit.preset.service.EditPresetServiceImpl

object EditModule {
    fun initialize() {
        EditServiceImpl.registerService()
        EditModulationsServiceImpl.registerService()
        EditPresetServiceImpl.registerService()
    }
}