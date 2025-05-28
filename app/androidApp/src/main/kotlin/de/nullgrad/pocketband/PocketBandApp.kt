package de.nullgrad.pocketband

import android.app.Application
import de.nullgrad.pocketband.di.AndroidApplication

class PocketBandApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidApplication = this
    }
}
