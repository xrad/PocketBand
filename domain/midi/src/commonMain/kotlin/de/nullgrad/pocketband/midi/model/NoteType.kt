package de.nullgrad.pocketband.midi.model

import androidx.compose.runtime.Immutable

@Immutable
enum class NoteType(val label: String, val beatFactor: Double) {
    Note32th("32th", 0.125),
    Note16th("16th", 0.25),
    Note8th("8th", 0.5),
    NoteQuarter("4th", 1.0),
    NoteHalf("Half", 2.0),
    NoteWhole("Whole", 4.0);

    companion object {
        fun fromDouble(value: Double): NoteType {
            val index = value.toInt()
            return entries.firstOrNull { it.ordinal == index } ?: NoteQuarter
        }
        fun fromDoubleString(value: String): NoteType {
            return fromDouble(value.toDouble())
        }
    }
}

val minNoteTypeIndex = NoteType.Note32th.ordinal // Use ordinal for enum index
val maxNoteTypeIndex = NoteType.NoteWhole.ordinal
