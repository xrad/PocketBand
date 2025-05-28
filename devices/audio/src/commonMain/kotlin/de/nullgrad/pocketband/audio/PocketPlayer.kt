package de.nullgrad.pocketband.audio

internal class PocketPlayer private constructor() {

    external fun create()
    external fun startAudio(bufferSize: Int, record: Boolean) : Boolean
    external fun stopAudio()
    external fun getSampleRate() : Int
    external fun writeBuffer(data: FloatArray)
    external fun readBuffer(data: FloatArray)
    external fun waitRMS() : Float

    companion object {
        val instance = PocketPlayer().also {
            it.create()
        }
    }
}
