package de.nullgrad.pocketband.synth.service

import de.nullgrad.pocketband.liveevents.model.TransportUpdate
import de.nullgrad.pocketband.plugins.model.PlayHead
import kotlin.math.floor

internal class TransportUpdater() {

    private var oneOverTimeSigNum = 0.0
    private var beatPosToBeatPos4 = 0.0

    fun start(playHead: PlayHead) {
        oneOverTimeSigNum = 1.0 / playHead.timeSigNum
        beatPosToBeatPos4 = 4.0 / playHead.timeSigDenom
    }

    fun getTransportUpdate(playHead: PlayHead): TransportUpdate {
        val beatPos4 = playHead.beatPos * beatPosToBeatPos4
        val beatPosFrac = beatPos4 - floor(beatPos4)
        val tick = beatPosFrac * playHead.ppq
        return TransportUpdate(
            milliseconds = (playHead.samplePos * playHead.secondsPerSample * 1000).toInt(),
            samplePos = playHead.samplePos,
            measure = floor(playHead.beatPos * oneOverTimeSigNum).toInt() + 1,
            beat = (playHead.beatPos % playHead.timeSigNum).toInt() + 1,
            tick = tick.toInt(),
        )
    }
}