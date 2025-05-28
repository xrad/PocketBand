package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.usecases.MidiKeyboardState
import de.nullgrad.pocketband.ui.keyboard.PadRow
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppIconButton

@Composable
fun KeyboardPaneSmall(
    keyboardStyleSelector: KeyboardStyleSelectionComposable,
    keyboardState: MidiKeyboardState,
    onTapDown: (key: MidiKey?) -> Unit,
    onTapUp: (key: MidiKey?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var settings by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        AnimatedVisibility(settings) {
            KeyboardSettingsPane(modifier = Modifier.fillMaxWidth(), orientation = Orientation.Horizontal)
        }
        Row(
            modifier = Modifier.height(80.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(modifier = Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                AppIconButton(
                    onClick = { settings = !settings },
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Keyboard Settings")
                }
                Spacer(modifier = Modifier.weight(1f))
                keyboardStyleSelector(Modifier)
            }
            Box(modifier = Modifier.weight(1f)) {
                ProvideTextStyle(Theme.fonts.labelSmall) {
                    PadRow(
                        keyboardState,
                        modifier = Modifier.fillMaxWidth(),
                        onTapDown = onTapDown,
                        onTapUp = onTapUp,
                    )
                }
            }
        }
    }
}

