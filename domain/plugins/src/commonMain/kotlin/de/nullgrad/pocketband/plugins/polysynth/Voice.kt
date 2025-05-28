package de.nullgrad.pocketband.plugins.polysynth

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.plugins.model.PlayHead

abstract class Voice {
    var key: MidiKey = 0
    var velocity: MidiVelocity = 0
    var state: VoiceState = VoiceState.Idle

    val isActive: Boolean
        get() = state == VoiceState.Playing || state == VoiceState.Release

    abstract suspend fun process(playHead: PlayHead, audioData: AudioData)

    open fun dispose() {
    }

    open fun start(playHead: PlayHead, key: MidiKey, velocity: MidiVelocity) {
        this.key = key
        this.velocity = velocity
        state = VoiceState.Playing
    }

    open fun release(playHead: PlayHead) {
        state = VoiceState.Release
    }

    open fun stop(playHead: PlayHead) {
        state = VoiceState.Idle
    }
}