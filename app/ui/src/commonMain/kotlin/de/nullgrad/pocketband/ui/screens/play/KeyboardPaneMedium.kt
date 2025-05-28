package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.usecases.MidiKeyboardState
import de.nullgrad.pocketband.ui.keyboard.PadGrid

@Composable
fun KeyboardPaneMedium(
    keyboardStyleSelector: KeyboardStyleSelectionComposable,
    keyboardState: MidiKeyboardState,
    onTapDown: (key: MidiKey?) -> Unit,
    onTapUp: (key: MidiKey?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier
                .width(intrinsicSize = IntrinsicSize.Max)
                .align(alignment = Alignment.CenterVertically)
        ) {
            KeyboardSettingsPane(modifier = Modifier.fillMaxWidth())
            keyboardStyleSelector(Modifier.fillMaxWidth())
        }
        PadGrid(
            keyboardState,
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.CenterVertically),
            onTapDown = onTapDown,
            onTapUp = onTapUp,
        )
    }
}
