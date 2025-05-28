package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import de.nullgrad.pocketband.midi.model.KeySignature
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppDropDownSelection

@Composable
fun EditKeySignature(
    keySignature: KeySignature,
    onScaleChange: (KeySignature) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AppDropDownSelection(
            modifier = Modifier.weight(1f),
            items = de.nullgrad.pocketband.midi.model.TonalKey.entries,
            itemContent = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.label,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.fonts.dialogContent,
                )
            },
            selectedIndex = de.nullgrad.pocketband.midi.model.TonalKey.entries.indexOf(keySignature.baseKey),
            onSelectItem = {
                val key = de.nullgrad.pocketband.midi.model.TonalKey.entries[it]
                onScaleChange(keySignature.copy(baseKey = key))
            }
        )

        AppDropDownSelection(
            modifier = Modifier.weight(1f),
            items = de.nullgrad.pocketband.midi.model.Mode.entries,
            itemContent = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.label,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.fonts.dialogContent,
                )
            },
            selectedIndex = de.nullgrad.pocketband.midi.model.Mode.entries.indexOf(keySignature.scale),
            onSelectItem = {
                val scale = de.nullgrad.pocketband.midi.model.Mode.entries[it]
                onScaleChange(keySignature.copy(scale = scale))
            }
        )

    }
}