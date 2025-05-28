package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.ui.keyboard.MidiKeyboardViewModel
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppToggleButton

@Composable
fun MidiKeyboardChordToggle(
    modifier: Modifier = Modifier,
    viewModel: MidiKeyboardViewModel = viewModel()
) {
    val playChords by viewModel.playChords.collectAsState()

    AppToggleButton(playChords, modifier = modifier,
        onSelectedChange = { viewModel.setPlayChords(!playChords) }) {
        Text("Chords", style = Theme.fonts.labelSmall)
    }
}