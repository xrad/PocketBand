package de.nullgrad.pocketband.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.ui.plugins.getParameterLabel
import de.nullgrad.pocketband.ui.screens.edit.ParameterViewModel
import de.nullgrad.pocketband.ui.screens.edit.rememberParameterValue

@Composable
fun ParameterToggle(
    paramKey: String,
    modifier: Modifier = Modifier,
    parameterViewModel: ParameterViewModel = rememberParameterValue(paramKey = paramKey)
) {
    val paramValue by parameterViewModel
    val paramLabel = remember {
        getParameterLabel(paramValue.parameter.owner.type, paramValue.parameter.key)
    }
    CheckBox(paramValue.asBool, paramLabel, modifier, onCheckedChange = { isChecked ->
        parameterViewModel.setBoolParameter(paramValue.parameter, isChecked)
    })
}


