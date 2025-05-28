package de.nullgrad.pocketband.plugins.polysynth

import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.plugins.model.PlayHead

class VoiceManager<T : Voice>(
    numVoices: Int,
    private val factory: () -> T,
) {
    private val _activeVoices = mutableListOf<T>()
    val activeVoices: List<T> = _activeVoices
    private val idleVoices = ArrayDeque<T>(numVoices)
    private val lookup = mutableMapOf<MidiKey, T?>()

    operator fun get(i: Int): T? {
        return if (i < activeVoices.size) activeVoices[i] else null
    }

    init {
        idleVoices.addAll(List(numVoices) { factory() })
    }

    fun dispose() {
        activeVoices.forEach { it.dispose() }
        idleVoices.forEach { it.dispose() }
    }

    fun startVoice(playHead: PlayHead, key: MidiKey, velocity: MidiVelocity) {
        findVoice(key)?.start(playHead, key, velocity)
    }

    private fun findVoice(key: MidiKey) : T? {
        // if this key is already playing, restart it
        var voice = lookup[key]
        if (voice != null) {
            return voice
        }

        // prefer an idle voice, obviously
        voice = idleVoices.removeFirstOrNull()
        if (voice != null) {
            _activeVoices.add(voice)
            lookup[key] = voice
            return voice
        }

        // steal from playing voices in the following order
        // - oldest which is released and is not the lowest or highest
        // - oldest which is playing and is not the lowest or highest
        var highest: Voice? = null
        var lowest: Voice? = null
        _activeVoices.forEach {
            if (highest == null || it.key > highest!!.key) highest = it
            if (lowest == null || it.key < lowest!!.key) lowest = it
        }

        voice = _activeVoices
            .filterNot { it == lowest || it == highest }
            .find { it.state == VoiceState.Release }
        if (voice != null) {
            lookup.remove(voice.key)
            _activeVoices.remove(voice)
            _activeVoices.add(voice)
            lookup[key] = voice
            return voice
        }

        voice = _activeVoices
            .filterNot { it == lowest || it == highest }
            .find { it.state == VoiceState.Playing }
        if (voice != null) {
            lookup.remove(voice.key)
            _activeVoices.remove(voice)
            _activeVoices.add(voice)
            lookup[key] = voice
            return voice
        }

        return null
    }

    fun releaseVoice(playHead: PlayHead, key: MidiKey) {
        val voice = lookup[key]
        voice?.release(playHead)
    }

    fun freeFinishedVoices() {
        var i = 0
        while (i < activeVoices.size) {
            val voice = activeVoices[i]
            if (voice.state != VoiceState.Idle) {
                i++
            } else {
                lookup.remove(voice.key)
                idleVoices.addLast(voice)
                _activeVoices.removeAt(i)
            }
        }
    }

    fun stopAndFreeAllVoices(playHead: PlayHead) {
        for (voice in activeVoices) {
            voice.stop(playHead)
        }
        freeFinishedVoices()
    }

    fun forAllVoices(lambda: (T) -> Unit) {
        activeVoices.forEach(lambda)
        idleVoices.forEach(lambda)
    }
}
