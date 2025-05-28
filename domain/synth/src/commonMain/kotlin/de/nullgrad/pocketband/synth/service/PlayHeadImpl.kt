package de.nullgrad.pocketband.synth.service

import de.nullgrad.pocketband.audio.model.defaultSampleRate
import de.nullgrad.pocketband.plugins.model.AudioTimestamp
import de.nullgrad.pocketband.plugins.model.PlayHead

internal class PlayHeadImpl(private val _isRunning: () -> Boolean) : PlayHead {

    private var _sampleRate = defaultSampleRate
    private var _secondsPerSample = 1.0 / defaultSampleRate
    private var _samplePos = 0
    private var _bpm = 100.0
    private var _samplesPerBeat = 0.0
    private var _beatsPerSample = 0.0
    private var _beatPos = 0.0
    private var _timeSigNum = 4
    private var _timeSigDenom = 4
    override val ppq = 96

    init {
        update()
    }

    override val isRunning: Boolean
        get() = _isRunning()

    override var sampleRate: Int
        get() = _sampleRate
        set(value) {
            _sampleRate = value
            update()
        }

    override val secondsPerSample: Double
        get() = _secondsPerSample

    override val samplePos: AudioTimestamp
        get() = _samplePos

    override var bpm: Double
        get() = _bpm
        set(value) {
            _bpm = value
            update()
        }

    override val samplesPerBeat: Double
        get() = _samplesPerBeat

    override val beatsPerSample: Double
        get() = _beatsPerSample

    override val beatPos: Double
        get() = _beatPos

    override var timeSigNum: Int
        get() = _timeSigNum
        set(value) { _timeSigNum = value }

    override var timeSigDenom: Int
        get() = _timeSigDenom
        set(value) { _timeSigDenom = value }

    private fun update() {
        _secondsPerSample = 1.0 / _sampleRate
        val bps = _bpm / 60.0
        _samplesPerBeat = _sampleRate / bps
        _beatsPerSample = 1.0 / _samplesPerBeat
    }

    override fun moveToStart() {
        _beatPos = 0.0
        _samplePos = 0
    }

    override fun forwardBySamples(numSamples: Int) {
        _samplePos += numSamples
        _beatPos += numSamples * _beatsPerSample
    }
}