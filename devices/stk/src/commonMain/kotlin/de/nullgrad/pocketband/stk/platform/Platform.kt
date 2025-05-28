package de.nullgrad.pocketband.stk.platform

internal interface Platform {
    fun initialize()
}

internal expect fun getPlatform(): Platform
