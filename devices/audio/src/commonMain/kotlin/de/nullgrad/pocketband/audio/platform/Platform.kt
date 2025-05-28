package de.nullgrad.pocketband.audio.platform

internal interface Platform {
    fun initialize()
}

internal expect fun getPlatform(): Platform