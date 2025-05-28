package de.nullgrad.pocketband.plugins.instruments

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.audioassets.AudioAssetsRepository
import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.liveevents.PluginUpdateProvider
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.midi.model.frequency
import de.nullgrad.pocketband.midi.model.maxMidiVelocity
import de.nullgrad.pocketband.midi.model.midiKeyC3
import de.nullgrad.pocketband.plugins.model.BoolPlugInParameter
import de.nullgrad.pocketband.plugins.model.DynamicIntPlugInParameter
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.SemitonesPlugInParameter
import de.nullgrad.pocketband.plugins.model.StringPlugInParameter
import de.nullgrad.pocketband.plugins.model.addBoolParameter
import de.nullgrad.pocketband.plugins.model.addDynamicIntParameter
import de.nullgrad.pocketband.plugins.model.addSemitoneParameter
import de.nullgrad.pocketband.plugins.model.addStringParameter
import de.nullgrad.pocketband.plugins.model.addVolumeParameter
import de.nullgrad.pocketband.plugins.nullPlugin
import de.nullgrad.pocketband.plugins.polysynth.PolySynth
import de.nullgrad.pocketband.plugins.polysynth.Voice
import de.nullgrad.pocketband.plugins.polysynth.VoiceManager
import de.nullgrad.pocketband.plugins.polysynth.VoiceState
import de.nullgrad.pocketband.plugins.toolbox.Adsr
import de.nullgrad.pocketband.plugins.toolbox.AdsrVoice
import de.nullgrad.pocketband.presets.model.PresetParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Immutable
data class SamplerLiveState(
    override val pluginId: Long = nullPlugin.id,
    val samplePos: List<Double> = listOf()
) : PluginUpdate

class SamplerVoice(private val synth: Sampler) : Voice() {

    internal var samplePos = -1.0
    private var sampleIncrement = 0.0
    private val envelope: AdsrVoice = AdsrVoice(synth.envelope)

    override fun start(playHead: PlayHead, key: MidiKey, velocity: MidiVelocity) {
        // reset phase, but not for restarting notes (stolen or duplicates)
        if (state != VoiceState.Playing) {
            samplePos = if (synth.reverse.boolValue) {
                synth.stopSample.effectiveValue
            } else {
                synth.startSample.effectiveValue
            }
        }
        envelope.start(playHead)
        super.start(playHead, key, velocity)
        rethinkDetune()
    }

    override fun release(playHead: PlayHead) {
        envelope.release(playHead)
        super.release(playHead)
    }

    override fun stop(playHead: PlayHead) {
        super.stop(playHead)
        samplePos = -1.0
    }

    internal fun rethinkDetune() {
        val detuneRange = synth.detune.effectiveValue / 12.0
        val detune = Math.pow(2.0, detuneRange)
        val tunedPitch = midiKeyC3.frequency
        val targetPitch = key.frequency * detune
        sampleIncrement = targetPitch / tunedPitch
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData) {
        val velocityGain = velocity / maxMidiVelocity.toDouble()
        val source = synth.audioFile
        if (source == null) {
            audioData.clear()
            return
        }
        var index = 0
        val gain = velocityGain * synth.volume.effectiveValue * envelope.level
        for (i in 0 until audioData.numFrames) {
            val sampleIndex = samplePos.toInt()
            for (j in 0 until audioData.numChannels) {
                audioData[index + j] += (source.soundData[j][sampleIndex] * gain).toFloat()
            }
            if (synth.reverse.boolValue) {
                samplePos -= sampleIncrement
                if (samplePos <= synth.startSample.effectiveValue) {
                    if (synth.loop.boolValue) {
                        samplePos = synth.stopSample.effectiveValue
                    } else {
                        stop(playHead)
                        break
                    }
                }
            } else {
                samplePos += sampleIncrement
                if (samplePos >= synth.stopSample.effectiveValue) {
                    if (synth.loop.boolValue) {
                        samplePos = synth.startSample.effectiveValue
                    } else {
                        stop(playHead)
                        break
                    }
                }
            }
            index += audioData.numChannels
            if (envelope.increment(playHead, state) == VoiceState.Idle) {
                stop(playHead)
                break
            }
        }
    }
}

class Sampler(
    id: Long,
    initializer: List<PresetParameter>,
) : PolySynth<SamplerVoice>(id), PluginUpdateProvider {

    companion object {
        const val PLUGIN_TYPE = "Sampler"
        const val KEY_DETUNE = "detune"
        const val KEY_VOLUME = "volume"
        const val KEY_LOOP = "loop"
        const val KEY_REVERSE = "reverse"
        const val KEY_START_SAMPLE = "startSample"
        const val KEY_STOP_SAMPLE = "stopSample"
        const val KEY_SAMPLE = "sample"

        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Sampler",
            kind = PlugInKind.Instrument,
            createPlugin = { id, initializer ->
                Sampler(id, initializer)
            }
        )
    }


    override val plugInDescriptor: PluginDescriptor
        get() = descriptor

    internal var envelope: Adsr = Adsr(this, initializer)
    internal var audioFile: AudioFile? = null
    private var newAudioFile: AudioFile? = null

    internal val detune: SemitonesPlugInParameter
    internal val volume: PlugInParameter
    internal val loop: BoolPlugInParameter
    internal val reverse: BoolPlugInParameter
    internal val startSample: DynamicIntPlugInParameter
    internal val stopSample: DynamicIntPlugInParameter
    internal val sample: StringPlugInParameter

    private var lastNumUsedVoices = 0
    private var needRethinkVoices = false
    private var needSwapSample = false

    private val audioAssetsRepository = LOCATOR.get<AudioAssetsRepository>()

    init {
        voiceManager = VoiceManager(8) { SamplerVoice(this) }

        detune = addSemitoneParameter(KEY_DETUNE, -24.0, 24.0, false, initializer, 0.0)
        volume = addVolumeParameter(KEY_VOLUME, 0.0, 1.0, initializer, 0.7)
        loop = addBoolParameter(KEY_LOOP, initializer, false)
        reverse = addBoolParameter(KEY_REVERSE, initializer, false)
        startSample = addDynamicIntParameter(KEY_START_SAMPLE, 0, Int.MAX_VALUE, initializer, 0)
        stopSample = addDynamicIntParameter(KEY_STOP_SAMPLE, 0, Int.MAX_VALUE, initializer, 0)
        sample = addStringParameter(KEY_SAMPLE, initializer, "")
        loadSample()
    }

    private fun loadSample() {
        CoroutineScope(Dispatchers.IO).launch {
            newAudioFile = if (sample.valueStr.isEmpty()) null
                else audioAssetsRepository.loadAudioFile(sample.valueStr)
            needSwapSample = true
        }
    }

    override fun onParameterChange(parameter: PlugInParameter) {
        super.onParameterChange(parameter)
        if (parameter.id == sample.id) {
            loadSample()
        }
    }

    private fun rethinkVoices() {
        voiceManager.forAllVoices { voice ->
            if (voice.isActive) {
                voice.rethinkDetune()
            }
        }
    }

    private fun swapSample() {
        audioFile = newAudioFile
        if (newAudioFile == null) {
            startSample.intValue = 0
            stopSample.intValue = 0
            startSample.max = 0.0
            stopSample.max = 0.0
            return
        }
        startSample.max = newAudioFile!!.numSamples.toDouble() - 1
        stopSample.max = newAudioFile!!.numSamples.toDouble() - 1
        startSample.intValue = 0
        stopSample.intValue = newAudioFile!!.numSamples - 1
        voiceManager.forAllVoices { voice ->
            if (voice.isActive) {
                voice.samplePos = startSample.effectiveValue
            }
        }
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        lastNumUsedVoices = 0
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        if (needRethinkVoices) {
            needRethinkVoices = false
            rethinkVoices()
        }
        if (needSwapSample) {
            needSwapSample = false
            swapSample()
        }
        if (audioFile?.numSamples != 0) {
            super.process(playHead, audioData, midiData)
        }
    }

    override fun getPluginUpdate(): PluginUpdate {
        val samplePos = voiceManager.activeVoices.map { it.samplePos }
        return SamplerLiveState(id, samplePos)
    }
}
