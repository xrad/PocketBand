package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.midi.model.maxMidiVelocity
import de.nullgrad.pocketband.midi.model.minMidiVelocity
import de.nullgrad.pocketband.ui.keyboard.MidiKeyboardViewModel
import de.nullgrad.pocketband.ui.widgets.DialInput
import de.nullgrad.pocketband.ui.widgets.Labelled
import kotlin.math.roundToInt

fun midiVelocityFromNormalized(v: Float): Int =
    minMidiVelocity + (maxMidiVelocity * v).roundToInt()

fun midiVelocityToNormalized(v: Int): Float =
    (v - minMidiVelocity) / (maxMidiVelocity - minMidiVelocity).toFloat()

@Composable
fun MidiKeyboardVelocity(
    modifier: Modifier = Modifier,
    viewModel: MidiKeyboardViewModel = viewModel()
) {
    Labelled("Velocity", modifier) {
        val velocity by viewModel.midiVelocity.collectAsState()
        DialInput(
            modifier = Modifier.padding(5.dp).width(40.dp).height(40.dp),
            value = midiVelocityToNormalized(velocity),
            onValueChanged = {
                viewModel.setMidiVelocity(midiVelocityFromNormalized(it))
            },
            onResetValue = { viewModel.resetMidiVelocity() },
        )
    }

}