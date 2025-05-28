package de.nullgrad.pocketband.midi

import de.nullgrad.pocketband.midi.service.MidiSettingsRepositoryImpl

object MidiModule {
    fun initialize() {
        MidiSettingsRepositoryImpl.registerService()
    }
}