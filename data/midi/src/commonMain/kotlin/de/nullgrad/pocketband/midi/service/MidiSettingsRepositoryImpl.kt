package de.nullgrad.pocketband.midi.service

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.midi.MidiSettingsRepository
import de.nullgrad.pocketband.midi.model.KeySignature
import de.nullgrad.pocketband.midi.model.Mode
import de.nullgrad.pocketband.midi.model.TimeSignature
import de.nullgrad.pocketband.midi.model.TonalKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private val defaultKeySignature = KeySignature(TonalKey.C, Mode.Ionic)
private const val defaultTempo = 100.0
private val defaultTimeSignature = TimeSignature(4, 4)

internal class MidiSettingsRepositoryImpl private constructor():
    MidiSettingsRepository {
    companion object {
        fun registerService() {
            LOCATOR.register(MidiSettingsRepository::class) {
                MidiSettingsRepositoryImpl()
            }
        }
    }

    private val _keySignature = MutableStateFlow(defaultKeySignature)
    override val keySignature = _keySignature.asStateFlow()

    private val _tempo = MutableStateFlow(defaultTempo)
    override val tempo = _tempo

    private val _timeSignature = MutableStateFlow(defaultTimeSignature)
    override val timeSignature = _timeSignature

    override fun setKeySignature(key: KeySignature) {
        _keySignature.value = key
    }

    override fun setTempo(tempo: Double) {
        _tempo.value = tempo
    }

    override fun setTimeSignature(timeSignature: TimeSignature) {
        _timeSignature.value = timeSignature
    }
}