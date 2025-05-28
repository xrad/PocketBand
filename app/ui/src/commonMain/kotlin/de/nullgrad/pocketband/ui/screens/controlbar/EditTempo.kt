package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun EditTempo(
    tempo: Double,
    onTempoChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pattern = remember { Regex("\\d+(\\.\\d+)?") }
    TextField(
        modifier = modifier,
        value = tempo.toString(),
        textStyle = Theme.fonts.dialogContent,
        onValueChange = {
            if (it.isNotEmpty() && it.matches(pattern)) {
                onTempoChange(it.toDouble())
            }
        }
    )
}