package de.nullgrad.pocketband.midi.model

import androidx.compose.runtime.Immutable

@Immutable
data class TimeSignature(
    val numerator: Int,
    val denominator: Int
)
