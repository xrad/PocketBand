package de.nullgrad.pocketband.ui.screens.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import de.nullgrad.pocketband.edit.preset.usecases.FormatPresetLabelUseCase
import de.nullgrad.pocketband.presets.model.PresetId
import de.nullgrad.pocketband.ui.theme.LocalContainerColor
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun PresetList(
    presets: List<PresetId>,
    selectedPresetId: Int,
    onSelectPreset: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val formatedPresets = remember(presets) {
        val formatPresetLabelUseCase = FormatPresetLabelUseCase()
        presets.map { formatPresetLabelUseCase(it.id, it.name) }
    }
    LazyColumn(
        modifier = modifier
            .padding(Theme.spacing.normal)
    ) {
        items(presets.size) {
            val presetLabel = formatedPresets[it]
            PresetItem(
                selected = it == selectedPresetId,
                label = presetLabel,
                onClick = {
                    onSelectPreset(it)
                }
            )
        }
    }
}

@Composable
private fun PresetItem(
    label: String,
    onClick: () -> Unit,
    selected: Boolean = false,
) {
    val color: Color
    val contentColor: Color
    if (selected) {
        color = Theme.colors.controls
        contentColor = LocalContainerColor.current
    }
    else {
        contentColor = Theme.colors.controls
        color = LocalContainerColor.current
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = Theme.shapes.medium,
            )
            .background(color = color)
            .clickable { onClick() }
            .padding(Theme.spacing.normal)
    ) {
        Text(label,
            color = contentColor,
            modifier = Modifier
                .background(color = color)
                .fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = Theme.fonts.selectionItem,
        )
    }
}
