package de.nullgrad.pocketband.presets.platform

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import de.nullgrad.pocketband.database.PocketBandDatabase
import de.nullgrad.pocketband.di.AndroidApplication

internal object PresetsAndroidPlatform : Platform {
    private val app: Application get() = AndroidApplication

    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = PocketBandDatabase.Schema,
            context = app.applicationContext,
            callback = object : AndroidSqliteDriver.Callback(PocketBandDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    //db.execSQL("PRAGMA foreign_keys=ON;")
                    db.setForeignKeyConstraintsEnabled(true)
                }
            },
            name = "test.db")
    }

}

internal actual fun getPlatform(): Platform = PresetsAndroidPlatform
