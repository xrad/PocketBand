package de.nullgrad.pocketband.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File

@Composable
actual fun rememberDataStore(): PrefsDataStore {
    return remember {
        createDataStore(
            producePath = {
                val userHome = System.getProperty("user.home")
                val prefsFile = File(userHome, ".pocketband.preferences_pb")
                prefsFile.absolutePath
            },
        )
    }
}