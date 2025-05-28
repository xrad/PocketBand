package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.plugins.model.PlugIn

class SetMuteModuleUseCase {
    private val setBoolParameterUseCase = SetBoolParameterUseCase()
    suspend operator fun invoke(pluginId: Long, mute: Boolean) {
        setBoolParameterUseCase(pluginId = pluginId, key = PlugIn.KEY_MUTE, value = mute)
    }
}