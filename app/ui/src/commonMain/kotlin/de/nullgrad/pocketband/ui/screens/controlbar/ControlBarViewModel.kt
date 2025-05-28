package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.engine.EngineController
import de.nullgrad.pocketband.midi.MidiSettingsRepository
import de.nullgrad.pocketband.midi.model.KeySignature
import de.nullgrad.pocketband.midi.model.TimeSignature
import kotlinx.coroutines.launch

class ControlBarViewModel : ViewModel() {
    private val engineController: EngineController = LOCATOR.get()
    private val midiSettingsRepository = LOCATOR.get<MidiSettingsRepository>()

    val engineMode = engineController.engineMode

    val tempo = midiSettingsRepository.tempo
    val timeSignature = midiSettingsRepository.timeSignature
    val keySignature = midiSettingsRepository.keySignature

    fun setTempo(tempo: Double) {
        viewModelScope.launch {
            midiSettingsRepository.setTempo(tempo)
        }
    }

    fun setTimeSignature(timeSignature: TimeSignature) {
        viewModelScope.launch {
            midiSettingsRepository.setTimeSignature(timeSignature)
        }
    }

    fun setKeySignature(keySignature: KeySignature) {
        viewModelScope.launch {
            midiSettingsRepository.setKeySignature(keySignature)
        }
    }

    fun startEngine() {
        viewModelScope.launch {
            engineController.startOutput()
        }
    }

    fun stopEngine() {
        viewModelScope.launch {
            engineController.stopEngine()
        }
    }
}