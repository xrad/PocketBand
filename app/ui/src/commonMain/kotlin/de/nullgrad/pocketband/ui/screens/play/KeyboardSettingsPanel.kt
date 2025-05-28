package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.Panel

@Composable
fun KeyboardSettingsPane(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Vertical,
) {
    when(orientation) {
        Orientation.Vertical -> KeyboardSettingsPaneVertical(modifier)
        Orientation.Horizontal -> KeyboardSettingsPaneHorizontal(modifier)
    }
}

@Composable
private fun KeyboardSettingsPaneVertical(modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Panel(modifier = Modifier.padding(Theme.spacing.small)) {
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                MidiKeyboardChordToggle(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally))
                MidiKeyboardScaleToggle(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally))
            }
        }
        Panel(modifier = Modifier.padding(Theme.spacing.small)) {
            MidiKeyboardOctave(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally))
        }
        Panel(modifier = Modifier.padding(Theme.spacing.small)) {
            MidiKeyboardVelocity(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun KeyboardSettingsPaneHorizontal(modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .height(90.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        Panel(modifier = Modifier.padding(Theme.spacing.small)) {
            Column(modifier = Modifier.align(Alignment.CenterVertically).width(80.dp),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                MidiKeyboardChordToggle(modifier = Modifier.fillMaxWidth())
                MidiKeyboardScaleToggle(modifier = Modifier.fillMaxWidth())
            }
        }
        Panel(modifier = Modifier.padding(Theme.spacing.small)) {
            MidiKeyboardOctave(modifier = Modifier
                .align(Alignment.CenterVertically))
        }
        Panel(modifier = Modifier.padding(Theme.spacing.small)) {
            MidiKeyboardVelocity(modifier = Modifier
                .align(Alignment.CenterVertically))
        }
    }
}
