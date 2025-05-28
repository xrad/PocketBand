package de.nullgrad.pocketband.presets

object PresetRepositoryModule {
    fun initialize() {
        DatabasePresetRepositoryImpl.registerService()
    }
}
