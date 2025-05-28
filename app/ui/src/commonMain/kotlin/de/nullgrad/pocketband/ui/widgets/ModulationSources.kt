package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.edit.uimodel.ParameterModulation
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.ui.dialogs.ListSelection
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.utils.buildParameterLabel
import de.nullgrad.pocketband.ui.utils.rememberTemporaryViewmodel

@Composable
fun ModulationSources(
    paramRef: ParameterRef,
    modifier: Modifier = Modifier,
) {
    var showAddSourceDialog by remember { mutableStateOf(false) }

    val modulationSourcesViewModel: ModulationSourceViewModel =
        rememberTemporaryViewmodel(key = paramRef.pluginId.toString() + paramRef.key) {
            ModulationSourceViewModel(paramRef)
        }
    val sources by modulationSourcesViewModel.sources.collectAsState()
    val allowedSourceParameters by modulationSourcesViewModel.allowedSourceParameters.collectAsState()

    ProvideTextStyle(Theme.fonts.labelSmall) {
        Column(modifier = modifier) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text("Mods", style = Theme.typography.titleSmall)
                SmallAppButton(onClick = {
                    showAddSourceDialog = true
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
            sources.forEach {
                ModulationSource(
                    modulation = it,
                    onRemoveModulation = { source -> modulationSourcesViewModel.removeModulation(source) },
                    onResetModulation = { source -> modulationSourcesViewModel.resetModulation(source) },
                    onSetModulation = { source, amt -> modulationSourcesViewModel.setModulation(source, amt) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
    if (showAddSourceDialog) {
        val items = remember(allowedSourceParameters) {
            allowedSourceParameters.map { buildParameterLabel(it) }
        }
        ListSelection(
            listItems = items,
            titleText = "Select source",
            onCancel = {
                showAddSourceDialog = false
            },
            onConfirm = { index ->
                val newSourceRef = allowedSourceParameters[index]
                modulationSourcesViewModel.addModulation(newSourceRef, 0.5)
                showAddSourceDialog = false
            }
        )
    }
}

@Composable
private fun ModulationSource(
    modulation: ParameterModulation,
    onRemoveModulation: (target: ParameterRef) -> Unit,
    onResetModulation: (target: ParameterRef) -> Unit,
    onSetModulation: (target: ParameterRef, Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = remember(modulation.source) {
        buildParameterLabel(modulation.source)
    }
    Panel(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.normal),
            ) {
            AppIconButton(
                modifier = Modifier
                    .weight(.1f),
                onClick = {
                    onRemoveModulation(modulation.source)
                }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
            Text(modifier = Modifier.weight(.5f), text = label, style = Theme.fonts.labelSmall)
//            Text(modifier = Modifier.weight(.1f),
//                text = percentFormatter(modulation.amount),
//                textAlign = TextAlign.End
//            )
            DialInput(
                modulation.amount.toFloat(),
                modifier = Modifier
                    .size(35.dp),
                onResetValue = {
                    onResetModulation(modulation.source)
                },
                onValueChanged = {
                    onSetModulation(modulation.source, it.toDouble())
                })
        }
    }
}
