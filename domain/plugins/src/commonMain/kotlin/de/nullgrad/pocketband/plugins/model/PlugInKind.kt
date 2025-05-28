package de.nullgrad.pocketband.plugins.model

import androidx.compose.runtime.Immutable

@Immutable
enum class PlugInKind {
    NoteEffect,
    Instrument,
    AudioEffect,
    Modulator,
    Undefined,
}