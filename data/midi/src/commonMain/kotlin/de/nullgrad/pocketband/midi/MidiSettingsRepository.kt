package de.nullgrad.pocketband.midi

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.midi.model.KeySignature
import de.nullgrad.pocketband.midi.model.TimeSignature
import kotlinx.coroutines.flow.StateFlow


interface MidiSettingsRepository : Service {
    val tempo: StateFlow<Double>
    val timeSignature: StateFlow<TimeSignature>
    val keySignature: StateFlow<KeySignature>

    fun setKeySignature(key: KeySignature)
    fun setTempo(tempo: Double)
    fun setTimeSignature(timeSignature: TimeSignature)
}

