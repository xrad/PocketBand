package de.nullgrad.pocketband.ui.screens.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.audio.model.RecordingStats
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.engine.EngineController
import de.nullgrad.pocketband.engine.EngineMode
import de.nullgrad.pocketband.ui.dialogs.AppDialog
import de.nullgrad.pocketband.ui.dialogs.TextEditDialog
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.utils.rememberTemporaryViewmodel
import de.nullgrad.pocketband.ui.widgets.AppButton

@Composable
fun RecordSampleDialog(
    engineMode: EngineMode,
    onClose: () -> Unit,
    engineController: EngineController = LOCATOR.get()
) {
    val recordingViewModel: RecordingViewModel = rememberTemporaryViewmodel()

    DisposableEffect(Unit) {
        engineController.startInputMonitor()
        onDispose {
            engineController.startOutput()
        }
    }

    AppDialog(
        onDismiss = { onClose() },
        title = "Record Sample",
        confirm = {
            TextButton(
                shape = Theme.shapes.medium,
                onClick = { onClose() },
            ) {
                Text(text = "Cancel")
            }
        },
        dismiss = {
            TextButton(
                shape = Theme.shapes.medium,
                onClick = {
                    if (engineMode == EngineMode.InputMonitor) {
                        engineController.startRecording()
                    }
                    else if (engineMode == EngineMode.Recording) {
                        engineController.stopRecording()
                    }
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FiberManualRecord,
                        tint = Theme.colors.controlsRecord,
                        contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    val label = if (engineMode == EngineMode.InputMonitor) "Record" else "Stop"
                    Text(text = label)
                }
            }
        }
    ) {
        val rms by recordingViewModel.audioInputRms.collectAsState()
        val recordingStats by recordingViewModel.recordingStats.collectAsState()
        RecordSampleView(
            rms,
            recordingStats,
            onResetRecording = recordingViewModel::resetRecording,
            onSaveRecording = recordingViewModel::saveRecordingAs,
        )
    }
}

@Composable
private fun RecordSampleView(
    rms: Float,
    recordingStats: RecordingStats,
    onResetRecording: () -> Unit,
    onSaveRecording: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.normal),
        modifier = Modifier
        .fillMaxWidth()
    ) {
        InputLevel(
            rms,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
        RecordingControl(
            recordingStats,
            onResetRecording = onResetRecording,
            onSaveRecording = onSaveRecording,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
}

@Composable
private fun RecordingControl(
    recordingStats: RecordingStats,
    onResetRecording: () -> Unit,
    onSaveRecording: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haveRecordedData by remember(recordingStats) {
        derivedStateOf { recordingStats.numSamples != 0 }
    }

    val colorControls = Theme.colors.controls
    val colorControlsDim = Theme.colors.controlsDim
    val statsColor = if (haveRecordedData) colorControls else colorControlsDim
    var saveRecordingDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .border(
            width = 1.dp,
            color = statsColor,
            shape = Theme.shapes.medium,
        )
        .padding(Theme.spacing.normal)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(3f))
                Column(modifier = Modifier.weight(7f)) {
                    Text("Length: %.1f s".format(recordingStats.duration), color = statsColor, style = Theme.fonts.labelSmall)
                    Text("Size: %d".format(recordingStats.numSamples), color = statsColor, style = Theme.fonts.labelSmall)
                    Text("Channels: %d".format(recordingStats.numChannels), color = statsColor, style = Theme.fonts.labelSmall)
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()) {
                AppButton(
                    modifier = Modifier,
                    enabled = haveRecordedData,
                    onClick = onResetRecording,
                ) {
                    Text("Reset")
                }
                AppButton(
                    modifier = Modifier,
                    enabled = haveRecordedData,
                    onClick = { saveRecordingDialog = true }) {
                    Text("Save")
                }
            }
        }
    }

    if (saveRecordingDialog) {
        TextEditDialog(
            title = "Save recording",
            initialValue = "rec",
            onConfirm = { name ->
                onSaveRecording(name)
                saveRecordingDialog = false
            },
            onDismiss = { saveRecordingDialog = false }
        )
    }
}

@Composable
private fun InputLevel(
    rms: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = LocalContentColor.current,
                shape = Theme.shapes.medium,
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputLevelBar(height = rms * 0.3f)
        InputLevelBar(height = rms * 0.7f)
        InputLevelBar(height = rms * 1.0f)
        InputLevelBar(height = rms * 0.7f)
        InputLevelBar(height = rms * 0.3f)
    }
}

@Composable
private fun InputLevelBar(
    height: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(12.dp)
            .fillMaxHeight(height)
            .background(color = Theme.colors.controlsAccent)
    )
}
