package de.nullgrad.pocketband.plugins.noteeffects

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.liveevents.LiveEventsService
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiKeyDown
import de.nullgrad.pocketband.midi.model.MidiKeyUp
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.midi.model.NoteType
import de.nullgrad.pocketband.midi.model.maxMidiOctave
import de.nullgrad.pocketband.midi.model.maxMidiVelocity
import de.nullgrad.pocketband.midi.model.midMidiVelocity
import de.nullgrad.pocketband.midi.model.minMidiKey
import de.nullgrad.pocketband.midi.model.minMidiOctave
import de.nullgrad.pocketband.midi.model.minMidiVelocity
import de.nullgrad.pocketband.plugins.model.BoolPlugInParameter
import de.nullgrad.pocketband.plugins.model.IntPlugInParameter
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addBoolParameter
import de.nullgrad.pocketband.plugins.model.addIntParameter
import de.nullgrad.pocketband.plugins.model.addNoteTypeParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.nullPlugin
import de.nullgrad.pocketband.presets.model.PresetParameter
import kotlin.math.floor

@Immutable
data class StepSequencerLiveState(
    override val pluginId: Long = nullPlugin.id,
    val step: Int = 0,
) : PluginUpdate

class StepSequencerStep(
    owner: PlugIn,
    initializer: List<PresetParameter>,
    val index: Int,
) {
    companion object {
        const val KEY_PITCH = "pitch"
        const val KEY_DURATION = "duration"
        const val KEY_VELOCITY = "velocity"
        const val KEY_ACTIVE = "active"
    }

    private val velocityParameter: IntPlugInParameter
    private val pitchParameter : IntPlugInParameter
    private val durationParameter : PlugInParameter
    private val activeParameter : BoolPlugInParameter

    private val keyDuration = "$KEY_DURATION$index"
    private val keyActive = "$KEY_ACTIVE$index"
    private val keyPitch = "$KEY_PITCH$index"
    private val keyVelocity = "$KEY_VELOCITY$index"

    var velocity: MidiVelocity
        get() { return velocityParameter.intValue }
        set(value) { velocityParameter.intValue = value }

    var duration: Double
        get() { return durationParameter.value }
        set(value) { durationParameter.value = value }

    var pitch: MidiKey
        get() { return pitchParameter.intValue }
        set(value) { pitchParameter.intValue = value }

    var isActive: Boolean
        get() { return activeParameter.boolValue }
        set(value) { activeParameter.boolValue = value }

    init {
        velocityParameter =
            owner.addIntParameter(keyVelocity, minMidiVelocity, maxMidiVelocity, initializer, midMidiVelocity)
        pitchParameter =
            owner.addIntParameter(keyPitch, 0, 7, initializer, minMidiKey)
        durationParameter =
            owner.addPercentParameter(keyDuration, initializer, 1.0)
        activeParameter =
            owner.addBoolParameter(keyActive, initializer, true)
    }
}

class StepSequencer(id: Long, initializer: List<PresetParameter>) : PlugIn(id) {
    companion object {
        const val PLUGIN_TYPE = "stepseq"
        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Step Sequencer",
            kind = PlugInKind.NoteEffect,
            createPlugin = { id, initializer -> StepSequencer(id, initializer) }
        )

        const val KEY_OCTAVE = "octave"
        const val KEY_STEP_DURATION = "duration"
        const val NUM_STEPS = 8
    }

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val liveEventsService = LOCATOR.get<LiveEventsService>()

    private val octave = addIntParameter(KEY_OCTAVE, minMidiOctave, maxMidiOctave, initializer, 5)

    private val steps: List<StepSequencerStep> = (0 until NUM_STEPS).map {
        StepSequencerStep(this, initializer, it)
    }

    private val stepDuration = addNoteTypeParameter(KEY_STEP_DURATION, initializer, NoteType.NoteQuarter)

    private var currentStep = 0
    private var lastStepPos = 0.0

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        currentStep = 0
        lastStepPos = -1.0
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        var step = steps[currentStep]
        var key = midiData.scale.getMidiKey(midiData.scaleKey, octave.intValue, step.pitch)
        var currentPos = playHead.beatPos
        var samplePos = playHead.samplePos
        val beatFactor = stepDuration.noteValue.beatFactor
        val keyPressDuration = floor((playHead.samplesPerBeat * beatFactor - 1) * step.duration).toInt()
        for (i in 0 until audioData.numFrames) {
            val delta = currentPos - lastStepPos
            if (delta >= beatFactor) {
                lastStepPos = currentPos
                if (step.isActive && key != null) {
                    midiData.insert(MidiKeyDown(timestamp = samplePos, key = key, velocity = step.velocity))
                    midiData.insert(MidiKeyUp(timestamp = samplePos + keyPressDuration, key = key, velocity = step.velocity))
                }
                if (liveEventsService.needsLiveUpdates(id)) {
                    liveEventsService.sendPluginUpdate(
                        StepSequencerLiveState(id, currentStep)
                    )
                }
                if (++currentStep >= NUM_STEPS) {
                    currentStep = 0
                    step = steps[currentStep]
                    key = midiData.scale
                        .getMidiKey(midiData.scaleKey, octave.intValue, step.pitch)
                }
            }
            currentPos += playHead.beatsPerSample
            ++samplePos
        }
    }
}
