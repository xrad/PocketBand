package de.nullgrad.pocketband.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.usecases.MidiKeyboardState
import de.nullgrad.pocketband.ui.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PadRow(
    keyboardState: MidiKeyboardState,
    onTapDown: (key: MidiKey?) -> Unit,
    onTapUp: (key: MidiKey?) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (keyboardState.scaleMode) {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            keyboardState.keys.forEach { scaleStep ->
                KeyboardPad(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    scaleStep = scaleStep,
                    onTapDown = onTapDown,
                    onTapUp = onTapUp,
                )
            }
        }
    }
    else {
        PianoRow(
            modifier = modifier
                .fillMaxWidth()
        ) {
            keyboardState.keys.forEach { scaleStep ->
                KeyboardPad(
                    modifier = Modifier.fillMaxSize().layoutId(scaleStep.isWhite),
                    scaleStep = scaleStep,
                    onTapDown = onTapDown,
                    onTapUp = onTapUp,
                    labelAlignment = Alignment.BottomCenter
                )
            }
        }
    }
}

@Composable
private fun PianoRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // white keys fill the keyboard width so they define the width of each key
        // height is taken from the incoming constraint, but black keys get half the
        // height
        val numWhites = measurables.count { true == it.layoutId }
        require(constraints.maxWidth != Constraints.Infinity)
        require(constraints.maxHeight != Constraints.Infinity)
        val allKeysWidth = constraints.maxWidth / numWhites
        val whiteKeyConstraints = constraints.copy(
            minWidth = allKeysWidth, maxWidth = allKeysWidth,
            minHeight = constraints.maxHeight, maxHeight = constraints.maxHeight)
        val blackKeysHeight = whiteKeyConstraints.maxHeight * 3 / 5
        val blackKeyConstraints = whiteKeyConstraints.copy(
            minHeight = blackKeysHeight, maxHeight = blackKeysHeight)

        // measure all keys
        val placeables = measurables.map { measurable ->
            if (true == measurable.layoutId) {
                measurable.measure(whiteKeyConstraints)
            }
            else {
                measurable.measure(blackKeyConstraints)
            }
        }

        // All keys are the same size, pick the first one
        val height = placeables[0].height
        layout(constraints.maxWidth, height) {
            var x = 0
            placeables.forEachIndexed { i, placeable ->
                val measurable = measurables[i]
                if (true == measurable.layoutId) {
                    placeable.place(x = x, y = 0, zIndex = 0f)
                    x += placeable.width
                }
                else {
                    placeable.place(x = x - placeable.width / 2, y = 0, zIndex = 1f)
                }
            }
        }
    }
}

@Preview
@Composable
private fun PadRowScalePreview() {
    val keyboardState = remember { buildScaleKeyboardState() }
    Theme {
        ProvideTextStyle(Theme.fonts.labelSmall) {
            PadRow(
                modifier = Modifier
                    .background(color = Color.Gray)
                    .size(400.dp, 100.dp),
                keyboardState = keyboardState,
                onTapUp = { },
                onTapDown = { }
            )
        }
    }
}

@Preview
@Composable
private fun PadRowPianoPreview() {
    val keyboardState = remember { buildNoScaleKeyboardState() }
    Theme {
        ProvideTextStyle(Theme.fonts.labelSmall) {
            PadRow(
                modifier = Modifier
                    .background(color = Color.Gray)
                    .size(400.dp, 100.dp),
                keyboardState = keyboardState,
                onTapUp = { },
                onTapDown = { }
            )
        }
    }
}
