package de.nullgrad.pocketband.stk

import de.nullgrad.pocketband.di.Service

interface Stk : Service {
    fun setSampleRate(sampleRate: Float)

    fun create(id: Int) : Int
    fun destroy(handle: Int)
    fun setParameter(handle: Int, paramId: Int, value: Double)

    fun tickProcess(handle: Int, audioData: FloatArray, offset: Int, numFrames: Int)
    fun tickInstrument(handle: Int, audioData: FloatArray, offset: Int, numFrames: Int)

    fun noteOn(handle: Int, frequency: Float, amplitude: Float)
    fun noteOff(handle: Int, amplitude: Float)
}
