package de.nullgrad.pocketband.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.usecases.MidiKeyboardState
import de.nullgrad.pocketband.ui.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

// map grid position to key index, scale mode
private val scaleKeyMap = listOf(
    6,  7,  8,
    3,  4,  5,
    0,  1,  2,
)

// map grid position to key index, no scale mode
private val noScaleKeyMap = listOf(
    8,  9,  10, 11,
    4,  5,   6,  7,
    0,  1,   2,  3,
)

@Composable
fun PadGrid(
    keyboardState: MidiKeyboardState,
    onTapDown: (key: MidiKey?) -> Unit,
    onTapUp: (key: MidiKey?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var preferHeight by remember { mutableStateOf(false) }

    val keyMap = if (keyboardState.scaleMode) scaleKeyMap else noScaleKeyMap

    Column(
        modifier = modifier
            .onSizeChanged { preferHeight = it.height < it.width }
            .padding(Theme.spacing.normal)
            //.aspectRatio(1f, matchHeightConstraintsFirst = preferHeight),
            ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier)
        val rows = 3
        val columns = keyboardState.keys.size / rows
        repeat(rows) { i ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                repeat(columns) { j ->
                    val n = keyMap[i * columns + j]
                    val scaleStep = keyboardState.keys[n]
                    KeyboardPad(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        scaleStep = scaleStep,
                        onTapDown = onTapDown,
                        onTapUp = onTapUp,
                    )
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
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PadGrid(
                modifier = Modifier
                    .background(color = Color.Gray)
                    .size(250.dp, 300.dp),
                keyboardState = keyboardState,
                onTapUp = { },
                onTapDown = { }
            )
            PadGrid(
                modifier = Modifier
                    .background(color = Color.Gray)
                    .size(300.dp, 250.dp),
                keyboardState = keyboardState,
                onTapUp = { },
                onTapDown = { }
            )
        }
    }
}

@Preview
@Composable
private fun PadRowNoScalePreview() {
    val keyboardState = remember { buildNoScaleKeyboardState() }
    Theme {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PadGrid(
                modifier = Modifier
                    .background(color = Color.Gray)
                    .size(300.dp, 250.dp),
                keyboardState = keyboardState,
                onTapUp = { },
                onTapDown = { }
            )
        }
    }
}
