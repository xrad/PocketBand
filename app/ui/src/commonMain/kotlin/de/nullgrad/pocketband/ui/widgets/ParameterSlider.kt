package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.screens.edit.ParameterViewModel
import de.nullgrad.pocketband.ui.screens.edit.rememberParameterValue

@Composable
fun ParameterSlider(
    paramKey: String,
    modifier: Modifier = Modifier,
    formatValue: ParameterValueFormatter? = null,
    parameterViewModel: ParameterViewModel = rememberParameterValue(paramKey = paramKey)
) {
    Box(modifier = modifier.size(120.dp, 40.dp)) {
        val paramValue by parameterViewModel
        Labelled(paramValueLabel(paramValue, formatValue)) {
            OverlayValue(value = paramValue.effectiveNormalizedValue) {
                SliderInput(
                    paramValue.normalizedValue.toFloat(),
                    onResetValue = {
                        parameterViewModel.resetParameter(paramValue.parameter)
                    },
                    onValueChanged = {
                        parameterViewModel.setParameterNormalized(paramValue.parameter, it.toDouble())
                    },
                    contextMenu = {
                        Panel(modifier = Modifier.fillMaxWidth(.5f)) {
                            ModulationSources(paramValue.parameter,
                                modifier = Modifier.fillMaxWidth())
                        }
                    }
                )
            }
        }
    }
}


