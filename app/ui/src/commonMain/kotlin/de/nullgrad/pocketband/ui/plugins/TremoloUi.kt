package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.processors.Tremolo
import de.nullgrad.pocketband.ui.widgets.ParameterEnum
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object TremoloUi : PluginUi {
    override val type = Tremolo.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        Tremolo.KEY_WAVEFORM to "Waveform",
        Tremolo.KEY_DEPTH to "Depth",
        Tremolo.KEY_FREQUENCY to "Frequency",
    )

    @Composable
    override fun createController() {
        TremoloUi()
    }
}

@Composable
fun TremoloUi() {
    ParameterRow {
        ParameterEnum(Tremolo.KEY_WAVEFORM, waveformLabels)
        ParameterSlider(Tremolo.KEY_FREQUENCY, formatValue = ::frequencyFormatter)
        ParameterSlider(Tremolo.KEY_DEPTH, formatValue = ::percentFormatter)
    }
}