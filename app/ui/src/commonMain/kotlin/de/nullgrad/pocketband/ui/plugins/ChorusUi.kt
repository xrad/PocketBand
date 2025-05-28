package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.processors.Chorus
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object ChorusUi : PluginUi {
    override val type = Chorus.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        Chorus.KEY_MIX to "Mix",
        Chorus.KEY_DEPTH to "Depth",
        Chorus.KEY_FREQUENCY to "Frequency",
    )

    @Composable
    override fun createController() {
        ChorusUi()
    }
}

@Composable
private fun ChorusUi() {
    ParameterRow {
        ParameterSlider(paramKey = Chorus.KEY_MIX, formatValue = ::percentFormatter)
        ParameterSlider(paramKey = Chorus.KEY_DEPTH, formatValue = ::percentFormatter)
        ParameterSlider(paramKey = Chorus.KEY_FREQUENCY, formatValue = ::frequencyFormatter)
    }
}