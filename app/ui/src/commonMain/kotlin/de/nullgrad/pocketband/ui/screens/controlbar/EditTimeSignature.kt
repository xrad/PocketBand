package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.midi.model.TimeSignature
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppIconButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditTimeSignature(
    timeSignature: TimeSignature,
    onTimeSignatureChange: (TimeSignature) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.normal)
    ) {
        TimeSigValue(timeSignature.numerator, 1, 99,
            onValueChange = {
                onTimeSignatureChange(timeSignature.copy(numerator = it))
            })
        Text(":", style = Theme.fonts.dialogContent)
        TimeSigValue(timeSignature.denominator, 1, 99,
            onValueChange = {
                onTimeSignatureChange(timeSignature.copy(denominator = it))
            })
    }
}

@Composable
private fun TimeSigValue(
    value: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AppIconButton(onClick = {
            onValueChange((value + 1).coerceIn(minValue, maxValue))
        }) {
            Icon(imageVector = Icons.Default.ArrowDropUp, contentDescription = "Increase")
        }
        Text(
            style = Theme.fonts.dialogContent,
            text = value.toString(),
        )
        AppIconButton(onClick = {
            onValueChange((value - 1).coerceIn(minValue, maxValue))
        }) {
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Increase")
        }
    }
}

@Preview
@Composable
fun TimeSigValuePreview() {
    Theme {
        var value by remember { mutableIntStateOf(1) }
        TimeSigValue(value, 1, 99, onValueChange = { value = it })
    }
}
