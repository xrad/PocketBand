package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import de.nullgrad.pocketband.plugins.instruments.Sampler
import de.nullgrad.pocketband.ui.utils.rememberTemporaryViewmodel
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider
import de.nullgrad.pocketband.ui.widgets.ParameterStringSelect
import de.nullgrad.pocketband.ui.widgets.ParameterToggle

object SamplerUi : PluginUi {
    override val type = Sampler.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        Sampler.KEY_DETUNE to "Detune",
        Sampler.KEY_LOOP to "Loop",
        Sampler.KEY_SAMPLE to "Sample",
        Sampler.KEY_VOLUME to "Volume",
        Sampler.KEY_REVERSE to "Reverse",
        Sampler.KEY_START_SAMPLE to "Start",
        Sampler.KEY_STOP_SAMPLE to "Stop",
    )

    @Composable
    override fun createController() {
        SamplerUi()
    }
}

@Composable
private fun SamplerUi() {
    ParameterColumn {
        Settings()
    }
}

@Composable
private fun Settings(
    viewModel : SamplerUIViewModel = rememberTemporaryViewmodel(),
) {
    val samples = viewModel.assets.collectAsState()
    ParameterColumn {
        ParameterRow {
            ParameterSlider(Sampler.KEY_DETUNE, formatValue = ::percentFormatter,)
            ParameterSlider(Sampler.KEY_VOLUME, formatValue = ::percentFormatter,)
        }
        ParameterRow {
            ParameterToggle(Sampler.KEY_LOOP)
            ParameterToggle(Sampler.KEY_REVERSE)
        }
        ParameterRow {
            ParameterStringSelect(
                Sampler.KEY_SAMPLE, samples.value.map { it.path },
                transform = { str ->
                    samples.value.firstOrNull { it.path == str }?.label ?: str
                }
            )
        }
        ParameterRow {
            VisualizerBox {
                WaveformEditor()
            }
        }
    }
}

