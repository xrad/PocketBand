package de.nullgrad.pocketband.stk

import de.nullgrad.pocketband.stk.platform.getPlatform
import de.nullgrad.pocketband.stk.service.StkImpl

object StkModule {
    fun initialize() {
        getPlatform().initialize()
        StkImpl.registerService()
    }
}
