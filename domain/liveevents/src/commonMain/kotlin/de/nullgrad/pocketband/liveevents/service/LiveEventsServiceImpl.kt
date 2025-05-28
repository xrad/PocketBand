package de.nullgrad.pocketband.liveevents.service

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.liveevents.LiveEventsService
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.liveevents.model.TransportUpdate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class LiveEventsServiceImpl : LiveEventsService {
    companion object {
        fun registerService() {
            LOCATOR.register(LiveEventsService::class) {
                LiveEventsServiceImpl()
            }
        }
    }

    // live updates. Set replay = 1 to avoid race between emitter and collector: the
    // collector will always receive at least one state instance. This is particularly
    // important for the forceUpdate case, ie. when a module's UI starts(!) processing
    // updates.
    private val _eventsFlow = MutableSharedFlow<PluginUpdate>(replay = 1)
    override val eventsFlow = _eventsFlow.asSharedFlow()

    private val _transportFlow = MutableSharedFlow<TransportUpdate>()
    override val transportFlow = _transportFlow.asSharedFlow()

    private val _subscriptions = mutableSetOf<Long>()
    override val subscriptions : Set<Long> = _subscriptions

    override fun subscribePluginUpdates(pluginId: Long) {
        _subscriptions.add(pluginId)
    }

    override fun unsubscribePluginUpdates(pluginId: Long) {
        _subscriptions.remove(pluginId)
    }

    override suspend fun sendPluginUpdate(pluginUpdate: PluginUpdate) {
        _eventsFlow.emit(pluginUpdate)
    }

    override fun needsLiveUpdates(pluginId: Long): Boolean {
        return subscriptions.contains(pluginId) && _eventsFlow.subscriptionCount.value != 0
    }

    override suspend fun sendTransportUpdate(event: TransportUpdate) {
        _transportFlow.emit(event)
    }
}