package de.nullgrad.pocketband.plugins.polysynth

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiEvent
import de.nullgrad.pocketband.midi.model.MidiKeyDown
import de.nullgrad.pocketband.midi.model.MidiKeyUp
import de.nullgrad.pocketband.midi.model.midiTimeLive
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn

abstract class PolySynth<T : Voice>(id: Long) : PlugIn(id) {

    lateinit var voiceManager: VoiceManager<T>

    protected fun initVoiceManager(voiceManager: VoiceManager<T>) {
        this.voiceManager = voiceManager
    }

    override fun dispose() {
        super.dispose()
        voiceManager.dispose()
    }

    val numUsedVoices: Int
        get() = voiceManager.activeVoices.size

    override fun stop(playHead: PlayHead) {
        voiceManager.stopAndFreeAllVoices(playHead)
        super.stop(playHead)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        var audio = audioData
        var sampleTime = playHead.samplePos
        for (event in midiData.events) {
            val audioWindowEnd = sampleTime + audio.numFrames
            val eventAudioTime = event.timestamp

            if (eventAudioTime == midiTimeLive) {
                // Handle live user inputs promptly
                handleMidiEvent(playHead, event)
                continue
            }

            if (eventAudioTime < sampleTime) {
                // Handle past events immediately
                handleMidiEvent(playHead, event)
                continue
            }

            if (eventAudioTime >= audioWindowEnd) {
                // Skip events beyond the render window
                break
            }

            // Process events within the render window
            val audioSamplesSlice = eventAudioTime - sampleTime
            val (slice, remaining) = audio.split(audioSamplesSlice)
            processAudioEvents(playHead, slice)
            handleMidiEvent(playHead, event)
            sampleTime += audioSamplesSlice
            audio = remaining
        }

        // Process remaining audio data (or all data if no midi events)
        processAudioEvents(playHead, audio)
    }

    private suspend fun processAudioEvents(playHead: PlayHead, audioData: AudioData) {
        for (voice in voiceManager.activeVoices) {
            if (voice.isActive) {
                voice.process(playHead, audioData)
            }
        }
        voiceManager.freeFinishedVoices()
    }

    private fun handleMidiEvent(playHead: PlayHead, event: MidiEvent) {
        when (event) {
            is MidiKeyDown -> voiceManager.startVoice(playHead, event.key, event.velocity)
            is MidiKeyUp -> voiceManager.releaseVoice(playHead, event.key)
        }
    }
}
