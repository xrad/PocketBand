package de.nullgrad.pocketband.midi.model

import androidx.compose.runtime.Immutable
import kotlin.math.pow

private val midiNoteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

fun midiKeyName(midiKey: Int): String {
    val octave = midiKey / midiNoteNames.size - 2
    val noteIndex = midiKey % midiNoteNames.size
    return "${midiNoteNames[noteIndex]}$octave"
}

fun midiKeyIsWhite(midiKey: Int): Boolean {
    val noteInOctave = midiKey % 12
    return when (noteInOctave) {
        0, 2, 4, 5, 7, 9, 11 -> true
        else -> false
    }
}

private fun midiKeyToFrequency(key: MidiKey): Double {
    return 440.0 * 2.0.pow((key - 69) / 12.0)
}

val midiKeyFrequencies = List(numMidiKeys) { midiKeyToFrequency(minMidiKey + it) }

val MidiKey.frequency: Double
    get() = midiKeyFrequencies[this]

private fun triadKeys(keySignature: KeySignature, key: MidiKey): List<MidiKey> {
    val result = mutableListOf(key)
    val chromaticKey = keySignature.chromaticKey(key)
    val step0 = keySignature.scale.keyToScaleStep[chromaticKey]
    val step1 = step0 + 2
    val step2 = step0 + 4
    val tonicKey = key - chromaticKey
    val key1 = tonicKey +
            keySignature.scale.scaleStepToKey[(step1 % 7)] + 12 * (step1 / 7) // Use division operator (/)
    val key2 = tonicKey +
            keySignature.scale.scaleStepToKey[(step2 % 7)] + 12 * (step2 / 7)
    result.add(key1)
    result.add(key2)
    return result.toList() // Convert mutable list to immutable list
}

@Immutable
enum class ChordType(val getKeys: (KeySignature, MidiKey) -> List<MidiKey>) {
    Triad(::triadKeys);
}
