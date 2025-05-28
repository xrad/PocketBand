package de.nullgrad.pocketband

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import de.nullgrad.pocketband.ui.screens.MainScreen
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun App(
    appViewModel: AppViewModel = AppViewModel()
) {
    AppLifecycleListener { event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> appViewModel.onPause()
            Lifecycle.Event.ON_RESUME -> appViewModel.onResume()
            else -> { }
        }
    }

    Theme {
        MainScreen()
    }
}
