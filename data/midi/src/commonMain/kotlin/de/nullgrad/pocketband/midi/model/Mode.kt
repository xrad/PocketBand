package de.nullgrad.pocketband.midi.model

import androidx.compose.runtime.Immutable

@Immutable
enum class Mode(
    val label: String,
    val tag: String,
    val scaleStepToKey: List<Int>,
    val keyToScaleStep: List<Int>)
{
    Ionic("Major", "maj", listOf(0, 2, 4, 5, 7, 9, 11), listOf(0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6)),
    Dorian("Dorian", "dor", listOf(0, 2, 3, 5, 7, 9, 10), listOf(0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6)),
    Phrygian("Phrygian", "phr", listOf(0, 1, 3, 5, 7, 8, 10), listOf(0, 1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6)),
    Lydian("Lydian", "lyd", listOf(0, 2, 4, 6, 7, 9, 11), listOf(0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6)),
    Mixolydian("Mixolydian", "mix", listOf(0, 2, 4, 5, 7, 9, 10), listOf(0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6)),
    Aeolian("Minor", "min", listOf(0, 2, 3, 5, 7, 8, 10), listOf(0, 0, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6)),
    Locrian("Locrian", "loc", listOf(0, 1, 3, 5, 6, 8, 10), listOf(0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6));

    val size: Int get() = scaleStepToKey.size

    fun getMidiKey(scaleKey: TonalKey, octave: MidiOctave, stepIndex: Int): MidiKey? {
        val baseKey = scaleKey.ordinal + 12 * (octave - minMidiOctave)
        var octaves = 0
        var stepi = stepIndex
        while (stepi >= size) {
            octaves += 12
            stepi -= size
        }
        val result = baseKey + octaves + scaleStepToKey[stepi]
        return if (result <= maxMidiKey) result else null
    }
}
