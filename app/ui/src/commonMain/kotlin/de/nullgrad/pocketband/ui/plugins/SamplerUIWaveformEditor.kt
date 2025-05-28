package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.plugins.instruments.SamplerLiveState
import de.nullgrad.pocketband.ui.screens.edit.EditPluginViewmodel
import de.nullgrad.pocketband.ui.screens.edit.rememberCurrentModule
import de.nullgrad.pocketband.ui.utils.rememberTemporaryViewmodel
import kotlinx.coroutines.flow.filterIsInstance

@Composable
private fun rememberSamplerLiveState(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) : State<SamplerLiveState> {
    return editPluginViewModel.liveEvents
        .filterIsInstance<SamplerLiveState>()
        .collectAsState(initial = SamplerLiveState())
}

@Composable
fun WaveformEditor() {
    val currentModule by rememberCurrentModule()

    val viewModel: WaveformEditorViewModel =
        rememberTemporaryViewmodel(key = currentModule.id.toString()) {
        WaveformEditorViewModel(currentModule)
    }

    val samplerLiveState by rememberSamplerLiveState()

    val waveformState by viewModel.waveformState.collectAsState()
    fun DrawScope.drawMarkerIfInRange(
        color: Color,
        samplePos: Int,
        sampleRangeStart: Int,
        sampleRangeEnd: Int,
        height: Float) {
        if (samplePos in sampleRangeStart..<sampleRangeEnd) {
            val x = (samplePos - sampleRangeStart).toFloat()
            val pp1 = Offset(x, height * .05f)
            val pp2 = Offset(x, height - 2*pp1.y)
            //println("pp1: $pp1 pp2: $pp2")
            drawLine(color = color, start = pp1, end = pp2, strokeWidth = 28f)
        }
    }

    Canvas(modifier = Modifier
        .pointerInput(Unit) {
            detectTransformGestures { centroid, pan, zoom, rotation ->
                viewModel.updateView(zoom, pan)
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(onDoubleTap = {
                viewModel.resetView()
            })
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                val e = awaitFirstDown()
                viewModel.tap(e.position)
                while (true) {
                    val event = awaitPointerEvent()
                    if (!waveformState.moving) {
                        break
                    }
                    if (event.type == PointerEventType.Move) {
                        event.changes.forEach {
                            viewModel.move(it.position)
                            it.consume()
                        }
                    } else if (event.type == PointerEventType.Release) {
                        break
                    }
                }
                viewModel.finishMove()
            }
        }
        .fillMaxWidth(.9f)
        .onSizeChanged {
            viewModel.setSize(it)
        }
        .height(300.dp)
    ) {
        val displayWidth = waveformState.displayWidth
        val displayOffsetEnd = waveformState.displayOffset + displayWidth

        val waveColor = Color.Red
        val maxAmp = size.height / 2
        val centerY = size.height / 2

        for (i in 0 until displayWidth) {
            val index = waveformState.displayOffset + i
            if (index >= waveformState.numScaledSamples) {
                // reached the end of available data
                break
            }
            val amp = (waveformState.waveform[index] * maxAmp).toFloat()
            val p1 = Offset(i.toFloat(), centerY - amp)
            val p2 = Offset(i.toFloat(), centerY + amp)
            drawLine(waveColor, p1, p2)
        }

        var markerColor: Color
        var markerPos: Int
        markerColor = if (waveformState.startMarker.moving) Color.Green else Color.Blue
        markerPos = waveformState.indexScaled(waveformState.startMarker.position)
        drawMarkerIfInRange(markerColor, markerPos, waveformState.displayOffset,
            displayOffsetEnd, size.height)
        markerColor = if (waveformState.stopMarker.moving) Color.Green else Color.Blue
        markerPos = waveformState.indexScaled(waveformState.stopMarker.position)
        drawMarkerIfInRange(markerColor, markerPos, waveformState.displayOffset,
            displayOffsetEnd, size.height)

        samplerLiveState.samplePos.forEach {
            //println("${samplerLiveState.samplePos.size} it: $it")
            markerColor = Color.Yellow
            drawMarkerIfInRange(markerColor, it.toInt(), waveformState.displayOffset,
                displayOffsetEnd, size.height)
        }

        /*
        val pos1 = waveformState.indexScaled(ld.value1.toInt());
        waveformState.startMarker.pos
        drawMarker(waveformState.startMarker,
            waveformState.displayOffset,
            displayOffsetEnd,
            size.height)

         */
        //drawMarker(canvas, markerPaint, waveformState.startMarker, waveformState.displayOffset, displayOffsetEnd, size.height)
        //drawMarker(canvas, markerPaint, waveformState.stopMarker, waveformState.displayOffset, displayOffsetEnd, size.height)

        /*
        val liveData = liveDataNotifier.value
        if (liveData.pluginId != LiveData.empty.pluginId) {
            drawMarker(canvas, playHeadsPaint, liveData.value1?.toInt()?.let { waveformState.indexScaled(it) }, waveformState.displayOffset, displayOffsetEnd, size.height)
            drawMarker(canvas, playHeadsPaint, liveData.value2?.toInt()?.let { waveformState.indexScaled(it) }, waveformState.displayOffset, displayOffsetEnd, size.height)
        }

         */
    }
}