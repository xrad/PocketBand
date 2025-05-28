package de.nullgrad.pocketband.liveevents

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.liveevents.model.TransportUpdate
import de.nullgrad.pocketband.liveevents.service.LiveEventsServiceImpl
import kotlinx.coroutines.flow.SharedFlow

interface PluginUpdateProvider {
    val id: Long
    fun getPluginUpdate() : PluginUpdate?
}

interface LiveEventsService : Service {
    val transportFlow: SharedFlow<TransportUpdate>
    val eventsFlow: SharedFlow<PluginUpdate>
    val subscriptions: Set<Long>
    suspend fun sendTransportUpdate(event: TransportUpdate)
    fun subscribePluginUpdates(pluginId: Long)
    fun unsubscribePluginUpdates(pluginId: Long)
    suspend fun sendPluginUpdate(pluginUpdate: PluginUpdate)
    fun needsLiveUpdates(pluginId: Long) : Boolean
}

object LiveEventsModule {
    fun initialize() {
        LiveEventsServiceImpl.registerService()
    }
}