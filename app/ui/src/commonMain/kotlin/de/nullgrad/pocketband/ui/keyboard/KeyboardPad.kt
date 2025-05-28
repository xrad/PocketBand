package de.nullgrad.pocketband.ui.keyboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.usecases.KeyboardKey
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.TouchPad

@Composable
fun KeyboardPad(
    scaleStep: KeyboardKey,
    onTapDown: (key: MidiKey?) -> Unit,
    onTapUp: (key: MidiKey?) -> Unit,
    modifier: Modifier = Modifier,
    labelAlignment: Alignment = Alignment.Center,
) {
    key(scaleStep) {
        TouchPad(
            modifier = modifier
                .padding(Theme.spacing.small),
            color = if (scaleStep.isTonic) Theme.colors.padColorTonic else
                if (scaleStep.isWhite) Theme.colors.padColor
                else Theme.colors.padColorContrast,
            onTapDown = { onTapDown(scaleStep.midiKey) },
            onTapUp = { onTapUp(scaleStep.midiKey) }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = labelAlignment) {
                Text(scaleStep.label,
                    color = Theme.colors.padLabel,
                    style = Theme.fonts.keyPad,
                    modifier = Modifier.wrapContentSize())
            }
        }
    }
}