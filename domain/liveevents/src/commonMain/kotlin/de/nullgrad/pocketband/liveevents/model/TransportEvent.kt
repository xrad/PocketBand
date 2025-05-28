package de.nullgrad.pocketband.liveevents.model

import androidx.compose.runtime.Immutable

interface TransportEvent

@Immutable
data class TransportUpdate(
    val milliseconds: Int = 0,
    val samplePos: Int = 0,
    val measure: Int = 0,
    val beat: Int = 0,
    val tick: Int = 0,
) : TransportEvent
