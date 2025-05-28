package de.nullgrad.pocketband.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import de.nullgrad.pocketband.platform.rememberDataStore
import de.nullgrad.pocketband.ui.dialogs.InfoDialog
import de.nullgrad.pocketband.ui.screens.controlbar.ControlBar
import de.nullgrad.pocketband.ui.screens.edit.EditScreen
import de.nullgrad.pocketband.ui.screens.manage.PresetsScreen
import de.nullgrad.pocketband.ui.screens.play.KeyboardPane
import de.nullgrad.pocketband.ui.screens.samples.SamplesScreen
import de.nullgrad.pocketband.ui.theme.Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class Screen {
    Presets,
    Edit,
    Samples
}

@Composable
fun MainScreen() {
    var screen by remember { mutableStateOf(Screen.Edit) }
    Scaffold(
        containerColor = Theme.colors.background,
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ControlBar(
                screen = screen,
                onChangeScreen = { newScreen -> screen = newScreen })
            ScreenSwitcher(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                screen = screen
            )
            KeyboardPane()
        }
    }
    val dataStore = rememberDataStore()
    var showWarningDialog by remember { mutableStateOf(false) }
    val keyWarningShown = "WarningShown"
    LaunchedEffect(Unit) {
        val key = booleanPreferencesKey(keyWarningShown)
        val prefs = dataStore.data.first()
        val warningShown = prefs[key]
        showWarningDialog = warningShown == null || !warningShown
    }
    val coroutineScope = rememberCoroutineScope()
    if (showWarningDialog) {
        InfoDialog(
            "Warning",
            "PocketBand is experimental. "
            +"Many functions are not completely implemented."
            +"The app may generate unexpected sounds which may cause discomfort or be harmful to your ears or audio equipment."
            +"\n\n"
            +"By closing this dialog you acknowledge that you have read and understood the warning."
            ,
            onFinished = {
                coroutineScope.launch {
                    dataStore.edit {
                        val key = booleanPreferencesKey(keyWarningShown)
                        it[key] = true
                    }
                }
                showWarningDialog = false
            }
        )
    }
}

@Composable
fun ScreenSwitcher(modifier: Modifier = Modifier, screen: Screen) {
    AnimatedContent(modifier = modifier, targetState = screen, label = "Screen switcher") {
        when(it) {
            Screen.Presets -> PresetsScreen()
            Screen.Edit -> EditScreen()
            Screen.Samples -> SamplesScreen()
        }
    }
}

