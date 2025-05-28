package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.plugins.modulators.LfoLiveState
import de.nullgrad.pocketband.plugins.modulators.ModLfo
import de.nullgrad.pocketband.plugins.toolbox.WaveTable
import de.nullgrad.pocketband.plugins.toolbox.WaveTableType
import de.nullgrad.pocketband.ui.screens.edit.EditPluginViewmodel
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterEnum
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider
import de.nullgrad.pocketband.ui.widgets.ParameterToggle
import kotlinx.coroutines.flow.filterIsInstance

object ModLfoUi : PluginUi {
    override val type = ModLfo.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        ModLfo.KEY_WAVEFORM to "Waveform",
        ModLfo.KEY_DEPTH to "Depth",
        ModLfo.KEY_FREQUENCY to "Frequency",
        ModLfo.KEY_OUTPUT to "Output",
        ModLfo.KEY_BIPOLAR to "Bipolar",
        ModLfo.KEY_FREERUNNING to "Free",
    )

    @Composable
    override fun createController() {
        ModLfoUi()
    }
}

@Composable
private fun ModLfoUi() {
    ParameterColumn(modifier = Modifier.padding(Theme.spacing.normal)) {
        ParameterRow {
            ParameterEnum(ModLfo.KEY_WAVEFORM, waveformLabels)
            ParameterSlider(ModLfo.KEY_FREQUENCY, formatValue = ::frequencyFormatter)
            ParameterSlider(ModLfo.KEY_DEPTH, formatValue = ::percentFormatter)
        }
        ParameterRow {
            ParameterColumn(modifier = Modifier.fillMaxWidth(0.3f)) {
                ParameterToggle(ModLfo.KEY_BIPOLAR)
                ParameterToggle(ModLfo.KEY_FREERUNNING)
            }
            ModLfoVisualizer(modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(100.dp))
        }
        ModulationTargets(ModLfo.KEY_OUTPUT)
    }
}

@Composable
private fun rememberLfoWavetableState(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) : WaveTable {
    val editPluginState = editPluginViewModel.state.collectAsState()

    @Immutable
    data class LfoParamState(
        val waveTableType: WaveTableType,
        val bipolar: Boolean,
        val depth: Double,
    )

    val paramState by remember {
        // edit model state can change frequently, so we use derivedStateOf to
        // isolate the changes we care about
        derivedStateOf {
            val waveform = editPluginState.value.getParamValue(ModLfo.KEY_WAVEFORM)
            val type = WaveTableType.entries[waveform.asInt]
            val bipolar = editPluginState.value.getParamValue(ModLfo.KEY_BIPOLAR)
            val depth = editPluginState.value.getParamValue(ModLfo.KEY_DEPTH)
            LfoParamState(type, bipolar.asBool, depth.value)
        }
    }

    return remember(paramState) {
        // whenever paramState has changed, recalculate the wave table
        WaveTable(128).also { wt ->
            wt.config(paramState.waveTableType, paramState.bipolar, paramState.depth)
        }
    }
}

@Composable
private fun rememberLfoLiveState(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) : State<LfoLiveState> {
    return editPluginViewModel.liveEvents
        .filterIsInstance<LfoLiveState>()
        .collectAsState(initial = LfoLiveState())
}

@Composable
private fun ModLfoVisualizer(
    modifier: Modifier = Modifier,
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) {
    val waveTable = rememberLfoWavetableState(editPluginViewModel = editPluginViewModel)
    val lfoState by rememberLfoLiveState(editPluginViewModel = editPluginViewModel)

    VisualizerBox {
        Canvas(
            modifier = modifier
        ) {
            val inset = 10f
            val outer = Rect(0f, 0f, size.width, size.height)
            val inner = outer.deflate(inset)

            val wavelength = inner.width
            val amplitude = inner.height / 2
            val centerY = inset + inner.height / 2

            // draw zero line
            drawLine(Color.White,
                Offset(inset, centerY), Offset(inset + wavelength, centerY),
                strokeWidth = 6f)

            // draw wavetable
            val resolution = 20
            var p1 = Offset.Zero
            for (i in 0..resolution) {
                val phase = i.toDouble() / resolution
                val mod = waveTable.getSample(phase)
                val p2 = Offset(inset + wavelength * phase.toFloat(),
                    centerY - amplitude * mod.toFloat())
                if (i == 0) {
                    p1 = p2
                    continue
                }
                drawLine(Color(0xFFFF9800),  p1, p2, strokeWidth = 6f)
                p1 = p2
            }

            // draw current output
            val center = Offset(inset + wavelength * lfoState.phase.toFloat(),
            centerY - amplitude * lfoState.value.toFloat())
            drawCircle(Color.White, radius = 12f, center = center, style = Fill)
        }
    }
}


