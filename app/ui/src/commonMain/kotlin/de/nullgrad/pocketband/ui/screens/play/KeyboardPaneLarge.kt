package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.usecases.MidiKeyboardState
import de.nullgrad.pocketband.ui.keyboard.PadGrid

@Composable
fun KeyboardPaneLarge(
    keyboardStyleSelector: KeyboardStyleSelectionComposable,
    keyboardState: MidiKeyboardState,
    onTapDown: (key: MidiKey?) -> Unit,
    onTapUp: (key: MidiKey?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            keyboardStyleSelector(Modifier)
            KeyboardSettingsPane(orientation = Orientation.Horizontal,
                modifier = Modifier.weight(1f))
        }
        PadGrid(
            keyboardState,
            modifier = Modifier
                .fillMaxSize()
                .align(alignment = Alignment.CenterHorizontally),
            onTapDown = onTapDown,
            onTapUp = onTapUp,
        )
    }
}