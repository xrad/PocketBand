package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.processors.LoPass
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object LoPassUi : PluginUi {
    override val type = LoPass.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        LoPass.KEY_CUTOFF to "Cut-Off",
    )

    @Composable
    override fun createController() {
        LoPassUi()
    }
}

@Composable
private fun LoPassUi() {
    ParameterRow {
        ParameterSlider(paramKey = LoPass.KEY_CUTOFF, formatValue = ::frequencyFormatter)
    }
}