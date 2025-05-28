package de.nullgrad.pocketband.presets.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import de.nullgrad.pocketband.database.PocketBandDatabase
import java.io.File
import java.util.Properties

internal object PresetsDesktopPlatform : Platform {

    override fun createDriver(): SqlDriver {
        val home = System.getProperty("user.home")
        val dbPath = File(home, ".pocketband.db").absolutePath
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath",
            Properties(1).apply { put("foreign_keys", "true") })
        try {
            PocketBandDatabase.Schema.create(driver)
        } catch (_: Exception) { }
        return driver
    }

}

internal actual fun getPlatform(): Platform = PresetsDesktopPlatform
