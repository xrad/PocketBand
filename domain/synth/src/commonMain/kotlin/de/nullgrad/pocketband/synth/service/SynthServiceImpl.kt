@file:OptIn(ExperimentalCoroutinesApi::class)

package de.nullgrad.pocketband.synth.service

import de.nullgrad.pocketband.audio.AudioOutput
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.midi.MidiSettingsRepository
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiKeyDown
import de.nullgrad.pocketband.midi.model.MidiKeyUp
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.midi.model.TimeSignature
import de.nullgrad.pocketband.midi.model.midiTimeLive
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PlugInModulation
import de.nullgrad.pocketband.stk.Stk
import de.nullgrad.pocketband.synth.SynthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

internal class SynthServiceImpl private constructor(): SynthService
{
    companion object {
        fun registerService() {
            LOCATOR.register(SynthService::class) {
                SynthServiceImpl()
            }
        }
    }

    private val stk = LOCATOR.get<Stk>()

    private val audioOutput : AudioOutput = LOCATOR.get()

    private val midiSettingsRepository = LOCATOR.get<MidiSettingsRepository>()

    private val midiData = MidiData()

    private val playHead: PlayHeadImpl = PlayHeadImpl { engineJob != null }

    private val modulations = SynthModulation()

    private val liveData = SynthLiveData()

    @OptIn(DelicateCoroutinesApi::class)
    private val engineDispatcher = newSingleThreadContext("AudioOutput")
    private val engineScope: CoroutineScope = CoroutineScope(SupervisorJob())
    private var engineJob: Job? = null

    private val chain: MutableList<PlugIn> = mutableListOf()
    private val modulators: MutableList<PlugIn> = mutableListOf()

    private var audioFilePlayback: AudioFilePlayback? = null

    private fun launchInEngine(block: suspend CoroutineScope.() -> Unit) : Job {
        return engineScope.launch(engineDispatcher, block = block)
    }

    private fun runInEngine(block: suspend CoroutineScope.() -> Unit) {
        return runBlocking(engineDispatcher, block = block)
    }

    init {
        launchInEngine {
            midiSettingsRepository.tempo.collect {
                setTempo(it)
            }
        }
        launchInEngine {
            midiSettingsRepository.timeSignature.collect {
                setTimeSignature(it)
            }
        }
        launchInEngine {
            midiSettingsRepository.keySignature.collect {
                midiData.setMidiScale(it.scale, it.baseKey)
            }
        }
    }

    override fun startOutput() {
        runInEngine {
            audioOutput.start()
            val sampleRate = audioOutput.sampleRate
            stk.setSampleRate(sampleRate.toFloat())
            playHead.sampleRate = sampleRate
            playHead.moveToStart()
            liveData.start(playHead)
            startPlugins(playHead = playHead)
        }
        engineJob = launchInEngine {
            while (true) {
                audioOutput.processOutput { buffer ->
                    processModulators(playHead, buffer, midiData)
                    modulations.apply()
                    processEffectChain(playHead, buffer, midiData)
                    audioFilePlayback?.let {
                        if (!it.addToBuffer(buffer)) {
                            stopAudioFileInternal()
                        }
                    }
                    playHead.forwardBySamples(buffer.numFrames)
                    midiData.consumeUntil(playHead.samplePos)
                    liveData.update(playHead, buffer)
                }
                yield()
            }
        }
    }

    override fun stopOutput() {
        runInEngine {
            stopPlugins(playHead = playHead)
            engineJob?.cancelAndJoin()
            engineJob = null
            audioOutput.stop()
        }
    }

    private fun startPlugins(playHead: PlayHead) {
        chain.forEach { it.start(playHead) }
        modulators.forEach { it.start(playHead) }
    }

    private fun stopPlugins(playHead: PlayHead) {
        chain.forEach { it.stop(playHead) }
        modulators.forEach { it.stop(playHead) }
    }

    override fun keyDown(key: MidiKey, velocity: MidiVelocity) {
        launchInEngine {
            midiData.insert(MidiKeyDown(timestamp = midiTimeLive, key = key, velocity = velocity))
        }
    }

    override fun keyUp(key: MidiKey, velocity: MidiVelocity) {
        launchInEngine {
            midiData.insert(MidiKeyUp(timestamp = midiTimeLive, key = key, velocity = velocity))
        }
    }

    override fun setPlugins(list: List<PlugIn>) {
        liveData.updateLiveDataProviders(list)
        launchInEngine {
            chain.clear()
            chain.addAll(list.filter { it.kind == PlugInKind.NoteEffect })
            chain.addAll(list.filter { it.kind == PlugInKind.Instrument })
            chain.addAll(list.filter { it.kind == PlugInKind.AudioEffect })
            modulators.clear()
            modulators.addAll(list.filter { it.kind == PlugInKind.Modulator })
        }
    }

    override fun startPlugin(plugin: PlugIn) {
        if (playHead.isRunning) {
            plugin.start(playHead) // Assuming start is implemented on PlugIn
        }
    }

    override fun stopPlugin(plugin: PlugIn) {
        if (playHead.isRunning) {
            plugin.stop(playHead) // Assuming start is implemented on PlugIn
        }
    }

    override fun setModulations(list: List<PlugInModulation>) {
        launchInEngine {
            modulations.setModulations(list)
        }
    }

    private suspend fun processEffectChain(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        audioData.clear()
        chain.forEach { fx ->
            if (!fx.muted) {
                fx.process(playHead, audioData, midiData)
            }
        }
    }

    private suspend fun processModulators(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        modulators.forEach { modulator ->
            if (!modulator.muted) {
                modulator.process(playHead, audioData, midiData)
            }
        }
    }

    private fun setTempo(tempo: Double) {
        playHead.bpm = tempo
    }

    private fun setTimeSignature(timeSignature: TimeSignature) {
        playHead.timeSigNum = timeSignature.numerator
        playHead.timeSigDenom = timeSignature.denominator
    }

    private val _audioFilePlaying = MutableStateFlow<AudioFile?>(null)
    override val audioFilePlaying = _audioFilePlaying.asStateFlow()

    override fun playAudioFile(audioFile: AudioFile) {
        val playback = AudioFilePlayback(audioFile)
        launchInEngine {
            audioFilePlayback = playback
            _audioFilePlaying.value = audioFile
        }
    }

    override fun stopAudioFile() {
        launchInEngine {
            stopAudioFileInternal()
        }
    }

    private fun stopAudioFileInternal() {
        audioFilePlayback = null
        _audioFilePlaying.value = null
    }

}

