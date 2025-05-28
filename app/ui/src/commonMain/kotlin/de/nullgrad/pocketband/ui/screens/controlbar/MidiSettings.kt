package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.midi.model.KeySignature
import de.nullgrad.pocketband.midi.model.TimeSignature
import de.nullgrad.pocketband.ui.dialogs.AppDialog
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun MidiSettings(
    tempo: Double,
    timeSignature: TimeSignature,
    keySignature: KeySignature,
    onTempoChange: (Double) -> Unit,
    onTimeSignatureChange: (TimeSignature) -> Unit,
    onScaleChange: (KeySignature) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editDialog by remember { mutableStateOf(false) }
    val originalTempo = remember { tempo }
    val originalTimeSignature = remember { timeSignature }
    if (editDialog) {
        AppDialog(
            title = "Score Settings",
            onConfirm = {
                editDialog = false
            },
            onDismiss = {
                onTempoChange(originalTempo)
                onTimeSignatureChange(originalTimeSignature)
                editDialog = false
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.normal)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Signature", modifier = Modifier.weight(.8f), style = Theme.fonts.dialogHeading)
                    EditTimeSignature(timeSignature, onTimeSignatureChange, modifier = Modifier.weight(1f))
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("Tempo", modifier = Modifier.weight(.8f), style = Theme.fonts.dialogHeading)
                    EditTempo(tempo, onTempoChange, modifier = Modifier.weight(1f))
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("Key", modifier = Modifier.weight(.8f), style = Theme.fonts.dialogHeading)
                    EditKeySignature(keySignature, onScaleChange, modifier = Modifier.weight(1f))
                }
            }
        }
    }

    Column(modifier = modifier.clickable {
            editDialog = true
        },
        //verticalArrangement = Arrangement.spacedBy(Theme.spacing.normal),
    ) {
        ShowTempo(tempo)
        ShowTimeSignature(timeSignature)
        ShowScale(keySignature)
    }
}

@Composable
fun ShowTimeSignature(
    timeSignature: TimeSignature,
) {
    Text(
        text = "${timeSignature.numerator}/${timeSignature.denominator}",
        style = Theme.fonts.displayTimeSignature,
    )
}

@Composable
private fun ShowTempo(
    tempo: Double,
) {
    Text(
        text = tempo.toString(),
        style = Theme.fonts.displayTempo,
    )
}

@Composable
private fun ShowScale(
    scale: KeySignature,
) {
    Text(
        text = scale.baseKey.label + scale.scale.tag,
        style = Theme.fonts.displayTempo,
    )
}

