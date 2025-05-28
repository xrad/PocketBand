package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.processors.PRCReverb
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object PRCReverbUi : PluginUi {
    override val type = PRCReverb.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        PRCReverb.KEY_MIX to "Mix",
        PRCReverb.KEY_T60 to "Decay Time",
    )

    @Composable
    override fun createController() {
        PRCReverbUi()
    }
}

@Composable
private fun PRCReverbUi() {
    ParameterRow {
        ParameterSlider(paramKey = PRCReverb.KEY_MIX, formatValue = ::percentFormatter)
        ParameterSlider(paramKey = PRCReverb.KEY_T60, formatValue = ::timingFormatter)
    }
}