package de.nullgrad.pocketband.ui.keyboard

import androidx.annotation.VisibleForTesting
import de.nullgrad.pocketband.midi.model.midiKeyC3
import de.nullgrad.pocketband.midi.model.midiKeyIsWhite
import de.nullgrad.pocketband.midi.model.midiKeyName
import de.nullgrad.pocketband.midi.usecases.KeyboardKey
import de.nullgrad.pocketband.midi.usecases.MidiKeyboardState

@VisibleForTesting
fun buildNoScaleKeyboardState() : MidiKeyboardState =
    MidiKeyboardState(
        scaleMode = false,
        keys = (0..12).map {
            KeyboardKey(
                index = it,
                midiKey = midiKeyC3 + it,
                label = midiKeyName(midiKeyC3 + it),
                isWhite = midiKeyIsWhite(midiKeyC3 + it),
                isTonic = false,
            )
        }
    )

@VisibleForTesting
fun buildScaleKeyboardState() : MidiKeyboardState =
    MidiKeyboardState(
        scaleMode = true,
        keys = listOf(0, 2, 4, 5, 7, 9, 11, 12, 14).map {
            KeyboardKey(
                index = it,
                midiKey = midiKeyC3 + it,
                label = midiKeyName(midiKeyC3 + it),
                isWhite = true,
                isTonic = false,
            )
        }
    )
