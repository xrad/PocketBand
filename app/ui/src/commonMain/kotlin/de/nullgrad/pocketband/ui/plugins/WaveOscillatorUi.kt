package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.instruments.WaveOscillator
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterEnum
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object WaveOscillatorUi : PluginUi {
    override val type = WaveOscillator.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        WaveOscillator.KEY_WAVEFORM to "Waveform",
        WaveOscillator.KEY_VOLUME to "Volume",
        WaveOscillator.KEY_DETUNE to "Detune",
        WaveOscillator.KEY_UNISON to "Unison",
        WaveOscillator.KEY_UNISON_WIDTH to "Unison Width",
    ) + adsrParameterLabels

    @Composable
    override fun createController() {
        WaveOscillatorUi()
    }
}

@Composable
private fun WaveOscillatorUi() {
    ParameterColumn {
        Settings()
        UnisonSettings()
        WaveOscVisualizer()
        AdsrUi()
    }
}

@Composable
private fun Settings() {
    ParameterRow {
        ParameterEnum(paramKey = WaveOscillator.KEY_WAVEFORM,
            strings = waveformLabels
        )
        ParameterSlider(
            paramKey = WaveOscillator.KEY_DETUNE,
            formatValue = ::percentFormatter,
        )
        ParameterSlider(
            paramKey = WaveOscillator.KEY_VOLUME,
            formatValue = ::volumeFormatter
        )
    }
}

@Composable
private fun WaveOscVisualizer() {

}

@Composable
private fun UnisonSettings() {
    ParameterRow {
        ParameterSlider(
            paramKey = WaveOscillator.KEY_UNISON,
            formatValue = ::intFormatter,
        )
        ParameterSlider(
            paramKey = WaveOscillator.KEY_UNISON_WIDTH,
            formatValue = ::percentFormatter
        )
    }
}
