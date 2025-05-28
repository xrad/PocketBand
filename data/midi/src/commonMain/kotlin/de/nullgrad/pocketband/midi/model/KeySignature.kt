package de.nullgrad.pocketband.midi.model

import androidx.compose.runtime.Immutable

@Immutable
data class KeySignature(
    val baseKey: TonalKey,
    val scale: Mode,
) {
    fun chromaticKey(key: MidiKey) : MidiKey = (key - baseKey.ordinal + 12) % 12
}