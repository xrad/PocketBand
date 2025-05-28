package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.ui.keyboard.MidiKeyboardViewModel
import de.nullgrad.pocketband.ui.widgets.AppIconButton
import de.nullgrad.pocketband.ui.widgets.Labelled
import org.jetbrains.compose.resources.painterResource
import de.nullgrad.pocketband.design.generaded.resources.Res
import de.nullgrad.pocketband.design.generaded.resources.minus_thick
import de.nullgrad.pocketband.design.generaded.resources.plus_thick

@Composable
fun MidiKeyboardOctave(
    modifier: Modifier = Modifier,
    viewModel: MidiKeyboardViewModel = viewModel()
) {
    Labelled("Octave", modifier.padding(2.dp)) {
        Row {
            AppIconButton(onClick = { viewModel.octaveDown() }) {
                Icon(painterResource(Res.drawable.minus_thick), contentDescription = null)
            }
            AppIconButton(onClick = { viewModel.octaveUp() }) {
                Icon(painterResource(Res.drawable.plus_thick), contentDescription = null)
            }
        }
    }
}