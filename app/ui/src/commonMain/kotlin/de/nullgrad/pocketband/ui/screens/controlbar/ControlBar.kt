package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.edit.preset.usecases.FormatPresetLabelUseCase
import de.nullgrad.pocketband.engine.EngineMode
import de.nullgrad.pocketband.ui.screens.Screen
import de.nullgrad.pocketband.ui.screens.manage.ManagePresetsViewModel
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppIconButton
import de.nullgrad.pocketband.ui.widgets.Panel
import org.jetbrains.compose.resources.painterResource
import de.nullgrad.pocketband.design.generaded.resources.Res
import de.nullgrad.pocketband.design.generaded.resources.waveform

@Composable
fun ControlBar(
    screen: Screen,
    onChangeScreen: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ControlBarViewModel = viewModel(),
    manageViewModel: ManagePresetsViewModel = viewModel()
) {
    val selectedPresetName by manageViewModel.presetName.collectAsState()
    val selectedPresetId by manageViewModel.presetId.collectAsState()
    val presets by manageViewModel.presetList.collectAsState()
    val isModified by manageViewModel.isModified.collectAsState()
    val tempo by viewModel.tempo.collectAsState()
    val timeSignature by viewModel.timeSignature.collectAsState()
    val engineMode by viewModel.engineMode.collectAsState()
    val keySignature by viewModel.keySignature.collectAsState()

    var showSaveAsDialog by remember {  mutableStateOf(false) }

    val presetLabel = remember(selectedPresetId, selectedPresetName) {
        val formatPresetLabelUseCase = FormatPresetLabelUseCase()
        formatPresetLabelUseCase(selectedPresetId, selectedPresetName)
    }

    Row(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Panel(contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(Theme.spacing.small)) {
            val targetScreen =
                if (screen == Screen.Edit) Screen.Presets else Screen.Edit
            val icon =
                if (screen == Screen.Edit) Icons.Default.Star else Icons.Default.Edit
            AppIconButton(modifier = Modifier.fillMaxHeight(),
                onClick = { onChangeScreen(targetScreen)
            }) {
                Icon(imageVector = icon, contentDescription = "Presets")
            }
        }
        Panel(modifier = Modifier
            .fillMaxHeight()
            .padding(Theme.spacing.small)) {
            AppIconButton(modifier = Modifier.fillMaxHeight(),
                onClick = { onChangeScreen(Screen.Samples) }) {
                Icon(painterResource(Res.drawable.waveform), contentDescription = "Samples")
            }
        }
        Panel(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(Theme.spacing.small)) {
            Row(modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.normal),
                verticalAlignment = Alignment.CenterVertically) {
                SavePatchButton(isModified = isModified,
                    onSave = { manageViewModel.savePreset() },
                    onSaveAs = { showSaveAsDialog = true }
                )
                EditablePatchName(presetName = selectedPresetName,
                    presetLabel = presetLabel,
                    onChangeName = { manageViewModel.renamePreset(it) })
            }
        }
        val playing = engineMode == EngineMode.Playing
        Panel(modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .padding(Theme.spacing.small),
            containerColor = if (playing) Theme.colors.panelColorAltSelected else Theme.colors.panelColor,
        ) {
            Transport(playing, viewModel::startEngine, viewModel::stopEngine)
        }
        Panel(modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .padding(Theme.spacing.small)) {
            MidiSettings(
                tempo = tempo,
                timeSignature = timeSignature,
                keySignature = keySignature,
                onTempoChange = viewModel::setTempo,
                onTimeSignatureChange = viewModel::setTimeSignature,
                onScaleChange = viewModel::setKeySignature,
            )
        }
    }

    if (showSaveAsDialog) {
        SaveAsDialog(
            presets = presets,
            defaultId = selectedPresetId,
            defaultName = selectedPresetName,
            onSave = { id, name ->
                manageViewModel.saveAsPreset(id, name)
                showSaveAsDialog = false
            },
            onDismiss = {
                showSaveAsDialog = false
            }
        )
    }
}

@Composable
private fun SavePatchButton(
    onSave: () -> Unit,
    onSaveAs: () -> Unit,
    isModified: Boolean,
) {
    AppIconButton(
        modifier = Modifier.height(height = 32.dp),
        onClick = onSave,
        onLongClick = onSaveAs,
        enabled = isModified,
    ) {
        Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
    }
}
