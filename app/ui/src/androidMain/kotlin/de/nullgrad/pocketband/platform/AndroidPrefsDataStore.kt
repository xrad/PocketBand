package de.nullgrad.pocketband.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberDataStore(): PrefsDataStore {
    val context = LocalContext.current
    return remember {
        createDataStore(
            producePath = {
                context.filesDir.resolve(dataStoreFileName).absolutePath
            },
        )
    }
}