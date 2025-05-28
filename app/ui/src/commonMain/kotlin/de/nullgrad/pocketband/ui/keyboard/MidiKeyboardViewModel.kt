package de.nullgrad.pocketband.ui.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.engine.EngineController
import de.nullgrad.pocketband.engine.EngineMode
import de.nullgrad.pocketband.midi.MidiSettingsRepository
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.midi.model.maxMidiOctave
import de.nullgrad.pocketband.midi.model.maxMidiVelocity
import de.nullgrad.pocketband.midi.model.minMidiOctave
import de.nullgrad.pocketband.midi.model.minMidiVelocity
import de.nullgrad.pocketband.midi.usecases.FlowKeyboardScaleStepsUseCase
import de.nullgrad.pocketband.synth.usecases.KeyOperation
import de.nullgrad.pocketband.synth.usecases.KeyUpDownUseCase
import de.nullgrad.pocketband.synth.usecases.PlayNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration

private const val defaultOctaveState = 2
private const val defaultMidiVelocity = maxMidiVelocity / 2 + minMidiVelocity
private const val defaultPlayChords = false
private const val defaultShowScale = true
private val defaultKeyboardStyle = KeyboardStyle.Medium

class MidiKeyboardViewModel : ViewModel() {

    private val engineController: EngineController = LOCATOR.get()

    private val midiSettingsRepository = LOCATOR.get<MidiSettingsRepository>()
    private val flowKeyboardScaleStepsUseCase = FlowKeyboardScaleStepsUseCase()

    private val keySignature = midiSettingsRepository.keySignature

    private val octave = MutableStateFlow(defaultOctaveState)

    private val _midiVelocity = MutableStateFlow(defaultMidiVelocity)
    val midiVelocity = _midiVelocity.asStateFlow()

    private val _playChords = MutableStateFlow(defaultPlayChords)
    val playChords = _playChords.asStateFlow()

    private val _showScale = MutableStateFlow(defaultShowScale)
    val showScale = _showScale.asStateFlow()

    private val _keyboardStyle = MutableStateFlow(defaultKeyboardStyle)
    val keyboardStyle = _keyboardStyle.combine(engineController.engineMode) {
        style, mode ->
        if (mode == EngineMode.Playing) {
            style
        }
        else {
            KeyboardStyle.None
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), defaultKeyboardStyle)

    // scale steps present a combined state using the current octave
    // and the current scale
    val keyboardScaleSteps = flowKeyboardScaleStepsUseCase(
        stateIn = viewModelScope,
        showScale = showScale,
        keySignature = keySignature,
        octave = octave)

    fun octaveUp() {
        val state = octave.value
        if (state < maxMidiOctave) {
            octave.value = state + 1
        }
    }

    fun octaveDown() {
        val state = octave.value
        if (state > minMidiOctave) {
            octave.value = state - 1
        }
    }

    fun setPlayChords(enableChords: Boolean) {
        _playChords.value = enableChords
    }

    fun setShowScale(showScale: Boolean) {
        _showScale.value = showScale
    }

    fun setKeyboardStyle(style: KeyboardStyle) {
        _keyboardStyle.value = style
    }

    private val playNoteUseCase = PlayNoteUseCase()
    fun playNote(key: MidiKey, duration: Duration) {
        viewModelScope.launch {
            playNoteUseCase(key, midiVelocity.value, playChords.value, duration)
        }
    }

    private val keyDownUseCase = KeyUpDownUseCase(operation = KeyOperation.KEY_DOWN)

    fun keyDown(key: MidiKey) {
        viewModelScope.launch {
            keyDownUseCase(key, midiVelocity.value, playChords.value)
        }
    }

    private val keyUpUseCase = KeyUpDownUseCase(operation = KeyOperation.KEY_UP)

    fun keyUp(key: MidiKey) {
        viewModelScope.launch {
            keyUpUseCase(key, midiVelocity.value, playChords.value)
        }
    }

    fun setMidiVelocity(velocity: MidiVelocity) {
        _midiVelocity.value = velocity
    }

    fun resetMidiVelocity() {
        _midiVelocity.value = defaultMidiVelocity
    }
}
