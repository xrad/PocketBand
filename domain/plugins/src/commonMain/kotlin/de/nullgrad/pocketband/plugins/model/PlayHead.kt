package de.nullgrad.pocketband.plugins.model

typealias AudioTimestamp = Int

interface PlayHead {

    val sampleRate: Int

    val secondsPerSample: Double
    val bpm: Double

    val samplePos: AudioTimestamp
    val beatPos: Double
    val samplesPerBeat: Double
    val beatsPerSample: Double
    val timeSigNum: Int
    val timeSigDenom: Int
    val ppq: Int

    fun moveToStart()
    fun forwardBySamples(numSamples: Int)
    val isRunning: Boolean
}