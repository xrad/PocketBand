package de.nullgrad.pocketband.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.ui.plugins.getParameterLabel

typealias ParameterValueFormatter = (Double) -> String

@Composable
fun paramValueLabel(
    paramValue: ParameterValue,
    formatValue: ParameterValueFormatter? = null,
) : String {
    val paramLabel = remember {
        getParameterLabel(paramValue.parameter.owner.type, paramValue.parameter.key)
    }
    if (formatValue == null) {
        return paramLabel
    }
    val valueLabel = formatValue(paramValue.value)
    return "$paramLabel $valueLabel"
}