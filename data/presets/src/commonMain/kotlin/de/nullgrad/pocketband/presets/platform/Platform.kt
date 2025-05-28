package de.nullgrad.pocketband.presets.platform

import app.cash.sqldelight.db.SqlDriver

internal interface Platform {
    fun createDriver(): SqlDriver
}

internal expect fun getPlatform(): Platform
