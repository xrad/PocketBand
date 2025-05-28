package de.nullgrad.pocketband.ui.screens.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.OutlinedAppButton

@Composable
fun PresetsScreen(
    modifier: Modifier = Modifier,
    viewModel: ManagePresetsViewModel = viewModel(),
) {
    val presets by viewModel.presetList.collectAsState()
    val selectedPresetId by viewModel.presetId.collectAsState()

    Row(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxWidth(0.7f)
            .fillMaxHeight()
            .padding(Theme.spacing.normal)
        ) {
            PresetList(presets, selectedPresetId, onSelectPreset = { id ->
                viewModel.loadPreset(id)
            })
        }
        Box(modifier = Modifier
            .wrapContentHeight()
            .padding(Theme.spacing.normal)
        ) {
            ActionButtons(viewModel)
        }
    }
}

@Composable
private fun ActionButtons(viewModel: ManagePresetsViewModel) {
    Column(modifier = Modifier
        .padding(Theme.spacing.normal),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.rows)) {
        val canPaste by viewModel.canPaste.collectAsState()
        OutlinedAppButton(modifier = Modifier
            .fillMaxWidth(),
            onClick = { viewModel.copyPreset() }) {
            Text("Copy")
        }
        OutlinedAppButton(modifier = Modifier
            .fillMaxWidth(),
            enabled = canPaste,
            onClick = { viewModel.pastePreset() }) {
            Text("Paste")
        }
        OutlinedAppButton(modifier = Modifier
            .fillMaxWidth(),
            onClick = { viewModel.resetPreset() }) {
            Text("Reset")
        }
    }

}
