package de.nullgrad.pocketband.audio.model

class AudioBlock(
    numFrames: Int,
    numChannels: Int,
    buffer: FloatArray, // Replace with appropriate data type in Kotlin
    sampleRate: Int,
    numSectionFrames: Int
) {
    val audioData: AudioData =
        AudioData(
            0,
            numFrames,
            numChannels,
            buffer,
            sampleRate
        )
    val sections = buildSections(numFrames, numSectionFrames)

    private fun buildSections(
        numFrames: Int,
        numSectionFrames: Int
    ): List<AudioData> {
        val numSections = numFrames / numSectionFrames
        require(numSections * numSectionFrames == numFrames) { "Invalid sectioning" }
        val sections = mutableListOf<AudioData>()
        for (i in 0 until numSections) {
            val section = audioData.slice(i * numSectionFrames, numSectionFrames)
            sections.add(section)
        }
        return sections
    }
}