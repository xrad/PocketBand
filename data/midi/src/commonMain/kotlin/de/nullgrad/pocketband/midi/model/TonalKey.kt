package de.nullgrad.pocketband.midi.model

import androidx.compose.runtime.Immutable

@Immutable
enum class TonalKey(val label: String) {
    C("C"),
    CSharp("C#"),
    D("D"),
    DSharp("D#"),
    E("E"),
    F("F"),
    FSharp("F#"),
    G("G"),
    GSharp("G#"),
    A("A"),
    ASharp("A#"),
    B("B");
}