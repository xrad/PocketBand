package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.instruments.FMVoice
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object FMVoiceUi : PluginUi {
    override val type = FMVoice.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        FMVoice.KEY_VOWEL to "Vowel",
        FMVoice.KEY_FORMANT to "Formant",
        FMVoice.KEY_VIBRATO_RATE to "Vibrato Rate",
        FMVoice.KEY_VIBRATO_AMOUNT to "Vibrato Amount",
    )

    @Composable
    override fun createController() {
        FMVoiceUi()
    }
}

@Composable
private fun FMVoiceUi() {
    ParameterColumn {
        ParameterRow {
            ParameterSlider(paramKey = FMVoice.KEY_VOWEL, formatValue = ::intFormatter)
            ParameterSlider(paramKey = FMVoice.KEY_FORMANT, formatValue = ::intFormatter)
        }
        ParameterRow {
            ParameterSlider(paramKey = FMVoice.KEY_VIBRATO_RATE, formatValue = ::intFormatter)
            ParameterSlider(paramKey = FMVoice.KEY_VIBRATO_AMOUNT, formatValue = ::intFormatter)
        }
    }
}
