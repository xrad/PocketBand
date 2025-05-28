package de.nullgrad.pocketband.stk.service

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.stk.Stk

internal class StkImpl : Stk {
    companion object {
        fun registerService() {
            LOCATOR.register(Stk::class) {
                StkImpl()
            }
        }
    }

    external override fun setSampleRate(sampleRate: Float)

    external override fun create(id: Int) : Int
    external override fun destroy(handle: Int)
    external override fun setParameter(handle: Int, paramId: Int, value: Double)

    external override fun tickProcess(handle: Int, audioData: FloatArray, offset: Int, numFrames: Int)
    external override fun tickInstrument(handle: Int, audioData: FloatArray, offset: Int, numFrames: Int)

    external override fun noteOn(handle: Int, frequency: Float, amplitude: Float)
    external override fun noteOff(handle: Int, amplitude: Float)
}
