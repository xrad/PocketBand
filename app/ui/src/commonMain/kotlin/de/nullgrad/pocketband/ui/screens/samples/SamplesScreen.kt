package de.nullgrad.pocketband.ui.screens.samples

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.audioassets.model.AudioAsset
import de.nullgrad.pocketband.engine.EngineMode
import de.nullgrad.pocketband.platform.getPlatform
import de.nullgrad.pocketband.ui.dialogs.ConfirmDialog
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppButton
import de.nullgrad.pocketband.ui.widgets.AppIconButton
import kotlin.math.sqrt

@Composable
fun SamplesScreen(
    modifier: Modifier = Modifier,
    samplesViewModel: SamplesViewModel = viewModel()
) {
    var showRecordDialog by remember {
        mutableStateOf(false)
    }

    val assetInfos by samplesViewModel.assetInfos.collectAsState()

    val assetPlaying by samplesViewModel.assetPlaying.collectAsState()

    val engineMode by samplesViewModel.engineMode.collectAsState()

    LazyColumn(modifier = modifier) {
        items(assetInfos) { assetInfo ->
            AudioAssetItem(
                assetInfo = assetInfo,
                assetPlaying = assetPlaying,
                showPlayButton = engineMode == EngineMode.Playing,
                onPlayAsset = samplesViewModel::playAudioAsset,
                onStopPlay = samplesViewModel::stopAudioAsset,
                onDeleteAsset = samplesViewModel::deleteAudioAsset,
            )
        }
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                AppButton(
                    onClick = {
                        showRecordDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp),
                ) {
                    Text("Record")
                }
            }
        }
    }
    if (showRecordDialog) {
        getPlatform().RequireRecordingPermissions(
            askUser = { onFinished ->
                ConfirmDialog(
                    titleText = "Permission required",
                    questionText = "Recording audio needs your permission. Please grant it to use this function.",
                    onFinished = onFinished,
                )
            }
        ) {
            RecordSampleDialog(
                engineMode = engineMode,
                onClose = {
                    showRecordDialog = false
                }
            )
        }
    }
}

@Composable
private fun AudioAssetItem(
    assetInfo: AudioAssetInfo,
    assetPlaying: AudioAsset?,
    showPlayButton: Boolean,
    onPlayAsset: (assetInfo: AudioAsset) -> Unit,
    onStopPlay: () -> Unit,
    onDeleteAsset: (assetInfo: AudioAsset) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            PlayStopUserSample(
                assetInfo.txtLabel,
                enabled = showPlayButton,
                onPlay = {
                    onPlayAsset(assetInfo.asset)
                },
                onStop = {
                    onStopPlay()
                },
                isPlaying = assetInfo.asset == assetPlaying,
            )
        },
        headlineContent = {
            Text(assetInfo.txtLabel, style = Theme.typography.bodyLarge)
        },
        supportingContent = {
            AssetSupportText(assetInfo)
        },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.normal),
                verticalAlignment = Alignment.CenterVertically) {
                WaveformThumb(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(.4f),
                    assetInfo = assetInfo,
                    color = Theme.colors.controlsDim)
                DeleteUserSample(assetInfo.txtLabel, assetInfo.asset.isBuiltIn,
                    onDelete = {
                        onDeleteAsset(assetInfo.asset)
                    })
            }
        }
    )
}

@Composable
private fun AssetSupportText(assetInfo: AudioAssetInfo) {
    val spanStyle = SpanStyle(color = Theme.colors.controlsDim,
        fontStyle = Theme.typography.bodySmall.fontStyle)
    Text(
        buildAnnotatedString {
            withStyle(style = spanStyle) {
                append(assetInfo.txtSource)
            }
            append(" · ")
            withStyle(style = spanStyle) {
                append(assetInfo.txtChannels)
            }
            append(" · ")
            withStyle(style = spanStyle) {
                append(assetInfo.txtDuration)
            }
        }
    )
}

@Composable
private fun PlayStopUserSample(
    name: String,
    enabled: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    AppIconButton(
        enabled = enabled,
        modifier = modifier.size(40.dp),
        onClick = { if (isPlaying) onStop() else onPlay() }
    ) {
        val playingState = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow
        AnimatedContent(targetState = playingState, label = "Play or stop sample $name") {
            Icon(imageVector = it, contentDescription = "Play or stop sample $name")
        }
    }
}

@Composable
private fun DeleteUserSample(
    name: String,
    isBuiltIn: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var ask by remember { mutableStateOf(false) }
    AppIconButton(
        modifier = modifier.size(40.dp),
        onClick = { ask = true }
    ) {
        if (!isBuiltIn) {
            Icon(imageVector = Icons.Default.Delete,
                contentDescription = "Delete sample $name")
        }
    }
    if (ask) {
        ConfirmDialog(
            "Delete sample",
            "Are you sure you want to delete sample $name?"

        ) { answer ->
            if (answer) onDelete()
            ask = false
        }
    }
}

@Composable
private fun WaveformThumb(
    color: Color,
    assetInfo: AudioAssetInfo,
    modifier: Modifier = Modifier,
) {
    val thumbnail = assetInfo.thumbnail.value
    Canvas(modifier = modifier) {
        val maxAmp = size.height / 2f
        val centerY = size.height / 2f
        val valuesPerPixel = thumbnail.size / size.width
        var inputIndex = 0
        for (i in 0 until size.width.toInt()) {
            val nextStop = (valuesPerPixel * (i + 1)).toInt().coerceAtMost(thumbnail.size)
            var sum = 0.0
            var count = 0
            while (inputIndex < nextStop) {
                val value = thumbnail[inputIndex]
                sum += value * value
                inputIndex++
                count++
            }
            val amp = sqrt(sum / count) * maxAmp
            drawRect(
                color = color,
                topLeft = Offset(i.toFloat(), centerY - amp.toFloat()),
                size = Size(1.0f, amp.toFloat() * 2f)
            )
        }
    }
}
