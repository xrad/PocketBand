package de.nullgrad.pocketband.synth.service

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.liveevents.LiveEventsService
import de.nullgrad.pocketband.liveevents.PluginUpdateProvider
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn

class SynthLiveData {
    companion object {
        private const val LIVE_UPDATE_RATE = 0.05
    }

    private val liveEventsService = LOCATOR.get<LiveEventsService>()

    private var liveUpdate = 0.0

    private val transportUpdater = TransportUpdater()

    private var updateProviders: List<PluginUpdateProvider> = emptyList()

    // prepare live data for playback
    fun start(playHead: PlayHead) {
        transportUpdater.start(playHead)
    }

    // to be called by main engine loop when a new buffer is ready.
    // if a new live update is due (as per LIVE_UPDATE_RATE) all
    // update providers will be polled for new data.
    suspend fun update(playHead: PlayHead, buffer: AudioData) {
        liveUpdate += buffer.numFrames * playHead.secondsPerSample
        if (liveUpdate > LIVE_UPDATE_RATE) {
            liveUpdate -= LIVE_UPDATE_RATE
            liveEventsService.sendTransportUpdate(
                transportUpdater.getTransportUpdate(playHead)
            )
            val subs = liveEventsService.subscriptions
            updateProviders
                .filter { subs.contains(it.id) }
                .forEach {
                    it.getPluginUpdate()?.let { liveEventsService.sendPluginUpdate(it) }
                }
        }
    }

    // find and cache live data providers when plugin list changes
    fun updateLiveDataProviders(list: List<PlugIn>) {
        updateProviders = list.filterIsInstance<PluginUpdateProvider>()
    }

}