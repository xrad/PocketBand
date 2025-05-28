package de.nullgrad.pocketband.midi.model

import androidx.compose.runtime.Immutable

class MidiData(events: List<MidiEvent> = emptyList()) {

    private val _mutableEvents = events.toMutableList()
    val events: List<MidiEvent> = _mutableEvents

    var scale: Mode = Mode.Ionic
    var scaleKey: TonalKey = TonalKey.C

    fun consumeUntil(time: MidiTimestamp) {
        _mutableEvents.removeIf { it.timestamp < time }
    }

    fun insert(event: MidiEvent) {
        if (_mutableEvents.isNotEmpty()) {
            for (i in 0 until _mutableEvents.size) {
                if (event.timestamp < _mutableEvents[i].timestamp) {
                    _mutableEvents.add(i, event)
                    return
                }
            }
        }
        // either empty or last
        _mutableEvents.add(event)
    }

    fun setMidiScale(scale: Mode, scaleKey: TonalKey) {
        this.scale = scale
        this.scaleKey = scaleKey
    }
}

/// Midi time events, use a dedicated type but effectively this is based on
/// sample rate
typealias MidiTimestamp = Int

const val midiTimeLive = -1

@Immutable
interface MidiEvent {
    val timestamp: MidiTimestamp
/*
    fun adjust(playHead: PlayHead): MidiEvent {
        if (timestamp >= 0) {
            return this
        }
        return adjust(playHead.samplePos - timestamp + 1)
    }

    fun adjust(timestamp: MidiTimestamp) : MidiEvent

 */
}

interface MidiKeyEvent : MidiEvent {
    val key: MidiKey
    val velocity: MidiVelocity
    val tag: Int
    fun clone(timestamp: MidiTimestamp, key: MidiKey, velocity: MidiVelocity, tag: Int) : MidiKeyEvent
}

@Immutable
data class MidiKeyDown(
    override val timestamp: MidiTimestamp,
    override val key: MidiKey,
    override val velocity: MidiVelocity,
    override val tag: Int = 0,
) : MidiKeyEvent {
    override fun clone(timestamp: MidiTimestamp, key: MidiKey, velocity: MidiVelocity, tag: Int): MidiKeyEvent {
        return this.copy(timestamp = timestamp, key = key, velocity = velocity, tag = tag)
    }
    /*
    override fun adjust(timestamp: MidiTimestamp): MidiKeyDown {
        return this.copy(timestamp = timestamp)
    }

     */
}

@Immutable
data class MidiKeyUp(
    override val timestamp: MidiTimestamp,
    override val key: MidiKey,
    override val velocity: MidiVelocity,
    override val tag: Int = 0,
) : MidiKeyEvent {
    override fun clone(timestamp: MidiTimestamp, key: MidiKey, velocity: MidiVelocity, tag: Int): MidiKeyEvent {
        return this.copy(timestamp = timestamp, key = key, velocity = velocity, tag = tag)
    }
    /*
    override fun adjust(timestamp: MidiTimestamp): MidiKeyUp {
        return this.copy(timestamp = timestamp)
    }

     */
}
