package de.nullgrad.pocketband.plugins.noteeffects

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.liveevents.LiveEventsService
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiKeyDown
import de.nullgrad.pocketband.midi.model.MidiKeyUp
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.midi.model.maxMidiKey
import de.nullgrad.pocketband.midi.model.maxMidiVelocity
import de.nullgrad.pocketband.midi.model.midiKeyC3
import de.nullgrad.pocketband.midi.model.minMidiKey
import de.nullgrad.pocketband.midi.model.minMidiVelocity
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addMidiKeyParameter
import de.nullgrad.pocketband.plugins.model.addTimingParameter
import de.nullgrad.pocketband.plugins.nullPlugin
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.sensors.model.SensorEvent
import de.nullgrad.pocketband.sensors.SensorService
import de.nullgrad.pocketband.sensors.model.SensorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.absoluteValue

@Immutable
data class ShakeEvent(val force: Float)

@Immutable
data class ShakeLiveUpdate(
    override val pluginId: Long = nullPlugin.id,
    val position: Float = 0f,
) : PluginUpdate

class Shaker(id: Long, initializer: List<PresetParameter>) : PlugIn(id)
{
    companion object {
        const val PLUGIN_TYPE = "Shaker"
        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Shaker",
            kind = PlugInKind.NoteEffect,
            createPlugin = { id, initializer -> Shaker(id, initializer) },
        )
        const val KEY_NOTE = "note"
        const val KEY_DURATION = "duration"
    }

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val note =
        addMidiKeyParameter(KEY_NOTE, minMidiKey, maxMidiKey, initializer, midiKeyC3)
    private val duration =
        addTimingParameter(KEY_DURATION, 10.0, 1000.0, initializer, 25.0)

    private val sensorManager = LOCATOR.get<SensorService>().sensorManager

    private val shakeDetector = ShakeDetector()
    private var shakeEvent: ShakeEvent? = null

    private val liveEventsService = LOCATOR.get<LiveEventsService>()
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)

    private val liveUpdateScope: CoroutineScope = CoroutineScope(dispatcher)

    private var lastLivePosition = .5f

    private fun eventListener(event: SensorEvent) {
        shakeDetector.processEvent(event)?.let {
            shakeEvent = it
        }
        if (liveEventsService.needsLiveUpdates(id)) {
            val livePosition = shakeDetector.position
            if ((lastLivePosition - livePosition).absoluteValue > 0.01) {
                lastLivePosition = livePosition
                liveUpdateScope.launch {
                    liveEventsService.sendPluginUpdate(
                        ShakeLiveUpdate(
                            pluginId = id,
                            position = livePosition,
                        )
                    )
                }
            }
        }
        else {
            lastLivePosition = .5f
        }
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        sensorManager.sensors.find { it.sensorType == SensorType.Accelerometer }?.listen(::eventListener)
    }

    override fun stop(playHead: PlayHead) {
        super.stop(playHead)
        sensorManager.sensors.find { it.sensorType == SensorType.Accelerometer }?.unlisten(::eventListener)
    }

    private fun ShakeEvent.velocity() : MidiVelocity =
        (maxMidiVelocity * force).toInt().coerceIn(minMidiVelocity, maxMidiVelocity)

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        shakeEvent?.let { event ->
            shakeEvent = null
            val velocity = event.velocity()
            val midiKey = note.effectiveValue.toInt()
            val durationInSamples =
                (duration.effectiveValue / 1000 * playHead.sampleRate).toInt()
            midiData.insert(
                MidiKeyDown(
                timestamp = playHead.samplePos,
                key = midiKey, velocity = velocity * maxMidiVelocity
                )
            )
            midiData.insert(
                MidiKeyUp(
                timestamp = playHead.samplePos + durationInSamples,
                key = midiKey, velocity = velocity * maxMidiVelocity
                )
            )
        }
    }

}