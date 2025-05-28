package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.processors.FreeVerb
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object FreeVerbUi : PluginUi {
    override val type = FreeVerb.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        FreeVerb.KEY_MIX to "Mix",
        FreeVerb.KEY_SIZE to "Size",
        FreeVerb.KEY_WIDTH to "Width",
    )

    @Composable
    override fun createController() {
        PRCReverbUi()
    }
}

@Composable
private fun PRCReverbUi() {
    ParameterRow {
        ParameterSlider(paramKey = FreeVerb.KEY_MIX, formatValue = ::percentFormatter)
        ParameterSlider(paramKey = FreeVerb.KEY_SIZE, formatValue = ::percentFormatter)
        ParameterSlider(paramKey = FreeVerb.KEY_WIDTH, formatValue = ::percentFormatter)
    }
}