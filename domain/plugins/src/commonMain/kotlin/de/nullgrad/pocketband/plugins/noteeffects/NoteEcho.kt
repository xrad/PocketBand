package de.nullgrad.pocketband.plugins.noteeffects

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiKeyEvent
import de.nullgrad.pocketband.midi.model.NoteType
import de.nullgrad.pocketband.midi.model.maxMidiKey
import de.nullgrad.pocketband.midi.model.midiTimeLive
import de.nullgrad.pocketband.midi.model.minMidiKey
import de.nullgrad.pocketband.plugins.model.NoteTypePlugInParameter
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addIntParameter
import de.nullgrad.pocketband.plugins.model.addNoteTypeParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.model.addSemitoneParameter
import kotlin.math.roundToInt

class NoteEcho(id: Long, initializer: List<PresetParameter>) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "note_echo"
        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Note Echo",
            kind = PlugInKind.NoteEffect,
            createPlugin = { id, initializer -> NoteEcho(id, initializer) },
        )
        const val KEY_DELAY = "delay"
        const val KEY_DECAY = "decay"
        const val KEY_PITCH = "pitch"
        const val KEY_NUM_ECHOES = "num_echoes"
    }

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val delay: NoteTypePlugInParameter =
        addNoteTypeParameter(KEY_DELAY, initializer, NoteType.NoteQuarter)
    private val decay: PlugInParameter =
        addPercentParameter(KEY_DECAY, initializer, 0.7)
    private val pitch: PlugInParameter =
        addSemitoneParameter(KEY_PITCH, -24.0, 24.0, true, initializer, 0.0)
    private val numEchos =
        addIntParameter(KEY_NUM_ECHOES, 0, 10, initializer, 0)

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        val windowEnd = playHead.samplePos + audioData.numFrames
        val keyEventsInWindow = midiData.events
            .takeWhile { it.timestamp < windowEnd }
            .filterIsInstance<MidiKeyEvent>()
        val numEchos = numEchos.effectiveValue.toInt()
        keyEventsInWindow.forEach { event ->
            // for each event, generate all echos in current window and(!) first one
            // outside current window. This allows effect to continue in future
            // windows
            var echoVel = event.velocity
            var echoTime =
                if (event.timestamp == midiTimeLive) playHead.samplePos else event.timestamp
            do {
                var tag = event.tag
                if (numEchos > 0) {
                    if (tag == numEchos) {
                        break
                    }
                    tag += 1
                }

                echoVel = (echoVel * decay.effectiveValue).toInt()
                if (echoVel == 0) {
                    break
                }
                val delayInSamples = (delay.noteValue.beatFactor * playHead.samplesPerBeat).toInt()
                echoTime += delayInSamples

                val echoKey = event.key + pitch.effectiveValue.roundToInt()
                if (echoKey < minMidiKey || echoKey > maxMidiKey) {
                    break
                }

                midiData.insert(event.clone(timestamp = echoTime, key = echoKey, velocity = echoVel, tag = tag))
            } while (echoVel > 0 && echoTime < windowEnd)
        }
    }
}
