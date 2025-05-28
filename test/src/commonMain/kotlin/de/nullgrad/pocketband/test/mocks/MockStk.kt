package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.stk.Stk

class MockStk : Stk {
    companion object {
        fun registerService() {
            LOCATOR.register(Stk::class) {
                MockStk()
            }
        }
    }

    override fun setSampleRate(sampleRate: Float) {
    }

    override fun create(id: Int): Int {
        return id
    }

    override fun destroy(handle: Int) {
    }

    override fun setParameter(handle: Int, paramId: Int, value: Double) {
    }

    override fun tickProcess(handle: Int, audioData: FloatArray, offset: Int, numFrames: Int) {
    }

    override fun tickInstrument(handle: Int, audioData: FloatArray, offset: Int, numFrames: Int) {
    }

    override fun noteOn(handle: Int, frequency: Float, amplitude: Float) {
    }

    override fun noteOff(handle: Int, amplitude: Float) {
    }
}