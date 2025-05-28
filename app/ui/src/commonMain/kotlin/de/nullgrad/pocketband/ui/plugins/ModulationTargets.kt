package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.edit.uimodel.ParameterModulation
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.ui.dialogs.ListSelection
import de.nullgrad.pocketband.ui.screens.edit.rememberCurrentModule
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.utils.buildParameterLabel
import de.nullgrad.pocketband.ui.utils.rememberTemporaryViewmodel
import de.nullgrad.pocketband.ui.widgets.AppIconButton
import de.nullgrad.pocketband.ui.widgets.Panel
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.SliderInput

@Composable
fun ModulationTargets(
    paramKey: String,
    title: (@Composable () -> Unit)? = null,
) {
    val currentModule by rememberCurrentModule()

    val modulationTargetsViewModel: ModulationTargetsViewModel =
        rememberTemporaryViewmodel(key = currentModule.id.toString() + paramKey) {
            ModulationTargetsViewModel(currentModule, paramKey)
        }

    val addModulationState by modulationTargetsViewModel.state.collectAsState()

    ParameterColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        if (title != null) {
            title()
        }
        else {
            Text(
                modifier = Modifier.fillMaxWidth(), text = "Modulation Targets",
                textAlign = TextAlign.Center
            )
        }
        addModulationState.modulationTargets.forEach { modulation ->
            ModulationTarget(
                modulation = modulation,
                onRemoveModulation = { target -> modulationTargetsViewModel.removeModulation(target) },
                onResetModulation = { target -> modulationTargetsViewModel.resetModulation(target) },
                onSetModulation = { target, amt -> modulationTargetsViewModel.setModulation(target, amt) },
            )
        }
        AppIconButton(
            onClick = {
                modulationTargetsViewModel.requestModuleSelection()
            }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }

    if (addModulationState.selectionMode == SelectionMode.Module) {
        ListSelection(
            listItems = addModulationState.allowedTargetModules.map { it.label },
            titleText = "Select target",
            onCancel = {
                modulationTargetsViewModel.cancelSelection()
            },
            onConfirm = { index ->
                val selectedModule = addModulationState.allowedTargetModules[index]
                modulationTargetsViewModel.requestParameterSelectionForModule(selectedModule)
            }
        )
    }
    else if (addModulationState.selectionMode == SelectionMode.Parameter) {
        val items = remember(addModulationState.allowedTargetParameters) {
            addModulationState.allowedTargetParameters.map { buildParameterLabel(it) }
        }
        ListSelection(
            listItems = items,
            titleText = "Select parameter",
            onCancel = {
                modulationTargetsViewModel.cancelSelection()
            },
            onConfirm = { index ->
                modulationTargetsViewModel.addModulation(
                    target = addModulationState.allowedTargetParameters[index],
                    amount = .5
                )
                modulationTargetsViewModel.cancelSelection()
            }
        )
    }

}

@Composable
private fun ModulationTarget(
    modulation: ParameterModulation,
    onRemoveModulation: (target: ParameterRef) -> Unit,
    onResetModulation: (target: ParameterRef) -> Unit,
    onSetModulation: (target: ParameterRef, Double) -> Unit,
) {
    Panel(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.normal)
        ) {
            val label = remember { buildParameterLabel(modulation.target) }
            Text(modifier = Modifier.weight(.5f), text = label)
            Text(modifier = Modifier.weight(.1f),
                text = percentFormatter(modulation.amount),
                textAlign = TextAlign.End
            )
            SliderInput(
                modulation.amount.toFloat(),
                modifier = Modifier
                    .weight(.2f)
                    .height(20.dp),
                onResetValue = {
                    onResetModulation(modulation.target)
                },
                onValueChanged = {
                    onSetModulation(modulation.target, it.toDouble())
                })
            AppIconButton(
                modifier = Modifier
                    .weight(.1f),
                onClick = {
                    onRemoveModulation(modulation.target)
                }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}
