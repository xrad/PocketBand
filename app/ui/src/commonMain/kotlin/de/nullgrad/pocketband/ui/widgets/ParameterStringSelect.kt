package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.screens.edit.ParameterViewModel
import de.nullgrad.pocketband.ui.screens.edit.rememberParameterValue
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun ParameterStringSelect(
    paramKey: String,
    strings: List<String>,
    modifier: Modifier = Modifier,
    parameterViewModel: ParameterViewModel = rememberParameterValue(paramKey = paramKey),
    transform: (String) -> String = { it }
) {
    val color = Theme.colors.controls
    Box(modifier = modifier.size(100.dp, 40.dp)) {
        val paramValue by parameterViewModel
        Labelled(paramValueLabel(paramValue)) {
            AppDropDownSelection(
                items = strings,
                selectedIndex = strings.indexOf(paramValue.valueStr).coerceAtLeast(0),
                itemContent = {
                    Text(
                        text = transform(it),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = color,
                        style = Theme.fonts.selectionItem,
                    )
                },
                onSelectItem = {
                    parameterViewModel.setStringParameter(paramValue.parameter, strings[it])
                })
        }
    }
}


