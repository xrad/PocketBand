package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.plugins.noteeffects.ShakeLiveUpdate
import de.nullgrad.pocketband.plugins.noteeffects.Shaker
import de.nullgrad.pocketband.ui.screens.edit.EditPluginViewmodel
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider
import kotlinx.coroutines.flow.filterIsInstance

object ShakerUi : PluginUi {
    override val type = Shaker.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        Shaker.KEY_NOTE to "Note",
        Shaker.KEY_DURATION to "Duration"
    )

    @Composable
    override fun createController() {
        ShakerUi()
    }
}

@Composable
private fun ShakerUi(modifier: Modifier = Modifier) {
    ParameterColumn(modifier = modifier) {
        ParameterRow {
            ParameterSlider(Shaker.KEY_NOTE, formatValue = ::midiKeyFormatter)
            ParameterSlider(Shaker.KEY_DURATION, formatValue = ::timingFormatter)
        }
        ParameterRow {
            ShakerVisualizer(
                modifier = Modifier
                    .width(100.dp)
                    .height(180.dp)
            )
        }
    }
}

@Composable
private fun rememberShakerLiveState(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) : State<ShakeLiveUpdate> {
    return editPluginViewModel.liveEvents
        .filterIsInstance<ShakeLiveUpdate>()
        .collectAsState(initial = ShakeLiveUpdate())
}

@Composable
fun ShakerVisualizer(
    modifier: Modifier = Modifier,
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) {
    val shakerState by rememberShakerLiveState(editPluginViewModel = editPluginViewModel)
    val shakerColor = Theme.colors.controls
    val shakerColorHit = Color(0xFFFF9800)
    val inset = with(LocalDensity.current) {
        Theme.spacing.normal.toPx()
    }
    val width = inset * 2

    VisualizerBox {
        Canvas(
            modifier = modifier
        ) {
            val position = shakerState.position
            val xpos = size.center.x + position * (size.width - width) / 2
            val color = if (position >= 0.99f || position < -0.99f)
                shakerColorHit else shakerColor
            drawRoundRect(color,
                topLeft = Offset(xpos-width/2, 0f),
                cornerRadius = CornerRadius(5f),
                size = Size(width, size.height))
        }
    }
}
