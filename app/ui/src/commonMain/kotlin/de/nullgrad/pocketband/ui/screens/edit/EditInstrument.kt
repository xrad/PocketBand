package de.nullgrad.pocketband.ui.screens.edit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.ui.dialogs.ConfirmDialog
import de.nullgrad.pocketband.ui.dialogs.ListSelection
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppIconButton
import de.nullgrad.pocketband.ui.widgets.SmallOutlinedAppButton
import de.nullgrad.pocketband.ui.widgets.tint

@Composable
fun EditInstrument(
    modifier: Modifier = Modifier,
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) {
    val currentModule by rememberCurrentModule()

    val selectModule = remember(currentModule) {
        fun(module: ModuleRef) {
            if (currentModule != module) {
                editPluginViewModel.setModule(module)
            }
            else {
                editPluginViewModel.clearModule()
            }
        }
    }

    Row(modifier) {
        Column(modifier = Modifier.weight(1f)) {
            InstrumentList(currentModule, selectModule)
            ModulatorList(currentModule, selectModule)
        }
        Column(modifier = Modifier.weight(1f)) {
            NoteFxList(currentModule, selectModule)
            AudioFxList(currentModule, selectModule)
        }
    }
}


@Composable
fun NoteFxList(
    currentModule: ModuleRef,
    onSelectModule: (ModuleRef) -> Unit,
    editInstrumentViewModel: EditInstrumentViewModel = viewModel(),
    pluginRegistry: de.nullgrad.pocketband.plugins.PlugInRegistry = remember { LOCATOR.get<de.nullgrad.pocketband.plugins.PlugInRegistry>() }
) {
    var showDeleteDialog by remember { mutableLongStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    val editInstrumentState by editInstrumentViewModel.state.collectAsState()

    EffectRack(
        title = {
            EffectRackTitle(text = "Note FX",
                action = {
                    SmallOutlinedAppButton(onClick = { showAddDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        list = editInstrumentState.noteEffects,
        renderItem = { entry ->
            val interactionSource = remember { MutableInteractionSource() }
            val modifier =
                if (editInstrumentState.noteEffects.size > 1)
                    Modifier.longPressDraggableHandle(interactionSource = interactionSource)
                else Modifier
            val selected = currentModule == entry.module
            val scale by animateFloatAsState(targetValue = if (selected) 1.1f else 1f, label = "scale")
            ModuleEffectSlot(
                modifier = modifier.tint(Theme.colors.controlsAccentTint, enabled = selected)
                    .scale(scale),
                module = entry.module,
                isMuted = entry.mute.asBool,
                onChangeMuted = { newState ->
                    editInstrumentViewModel.setMute(entry.module, newState)
                },
                onSelectModule = onSelectModule,
                specialAction = {
                    AppIconButton(onClick = { showDeleteDialog = it.id }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
            )
        },
        reorderItem = { moveId, beforeId ->
            editInstrumentViewModel.reorderProcessors(moveId, beforeId)
        },
    )

    if (showAddDialog) {
        ListSelection(
            listItems = pluginRegistry.noteFxLabels,
            titleText = "Add note effect",
            onCancel = {
                showAddDialog = false
            },
            onConfirm = { index ->
                val type = pluginRegistry.noteEffects[index].type
                editInstrumentViewModel.createProcessor(type)
                showAddDialog = false
            })
    }

    if (showDeleteDialog != 0L) {
        ConfirmDialog(
            titleText = "Delete Note FX",
            questionText = "Really delete this note effect?",
            onFinished = { answer ->
                if (answer) {
                    editInstrumentViewModel.deleteProcessor(showDeleteDialog)
                }
                showDeleteDialog = 0
            }
        )
    }
}

@Composable
fun InstrumentList(
    currentModule: ModuleRef,
    onSelectModule: (ModuleRef) -> Unit,
    editInstrumentViewModel: EditInstrumentViewModel = viewModel(),
    pluginRegistry: de.nullgrad.pocketband.plugins.PlugInRegistry = remember { LOCATOR.get<de.nullgrad.pocketband.plugins.PlugInRegistry>() }
) {
    var showReplaceDialog by remember { mutableLongStateOf(0) }
    val editInstrumentState by editInstrumentViewModel.state.collectAsState()

    EffectRack(
        title = {
            EffectRackTitle(text = "Source")
        },
        list = editInstrumentState.instruments,
        renderItem = { entry ->
            val selected = currentModule == entry.module
            val scale by animateFloatAsState(targetValue = if (selected) 1.1f else 1f, label = "scale")
            ModuleEffectSlot(
                modifier = Modifier.tint(Theme.colors.controlsAccentTint, enabled = selected)
                    .scale(scale),
                module = entry.module,
                isMuted = entry.mute.asBool,
                onChangeMuted = { newState ->
                    editInstrumentViewModel.setMute(entry.module, newState)
                },
                onSelectModule = onSelectModule,
                specialAction = {
                    AppIconButton(onClick = {
                        showReplaceDialog = it.id
                    }) {
                        Icon(imageVector = Icons.Default.Cached, contentDescription = "Replace")
                    }
                },
            )
        }
    )

    if (showReplaceDialog != 0L) {
        ListSelection(
            listItems = pluginRegistry.instrumentLabels,
            titleText = "Replace Source",
            onCancel = {
                showReplaceDialog = 0
            },
            onConfirm = {
                val type = pluginRegistry.instruments[it].type
                editInstrumentViewModel.replaceProcessor(type, showReplaceDialog)
                showReplaceDialog = 0
            })
    }
}

@Composable
fun AudioFxList(
    currentModule: ModuleRef,
    onSelectModule: (ModuleRef) -> Unit,
    editInstrumentViewModel: EditInstrumentViewModel = viewModel(),
    pluginRegistry: de.nullgrad.pocketband.plugins.PlugInRegistry = remember { LOCATOR.get<de.nullgrad.pocketband.plugins.PlugInRegistry>() }
) {
    var showDeleteDialog by remember { mutableLongStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    val editInstrumentState by editInstrumentViewModel.state.collectAsState()

    EffectRack(
        title = {
            EffectRackTitle(text = "Audio FX",
                action = {
                    SmallOutlinedAppButton(onClick = { showAddDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        list = editInstrumentState.audioEffects,
        renderItem = { entry ->
            val interactionSource = remember { MutableInteractionSource() }
            val modifier =
                if (editInstrumentState.audioEffects.size > 1)
                    Modifier.longPressDraggableHandle(interactionSource = interactionSource)
                else Modifier
            val selected = currentModule == entry.module
            val scale by animateFloatAsState(targetValue = if (selected) 1.1f else 1f, label = "scale")
            ModuleEffectSlot(
                modifier = modifier.tint(Theme.colors.controlsAccentTint, enabled = selected)
                    .scale(scale),
                module = entry.module,
                isMuted = entry.mute.asBool,
                onChangeMuted = { newState ->
                    editInstrumentViewModel.setMute(entry.module, newState)
                },
                onSelectModule = onSelectModule,
                specialAction = {
                    AppIconButton(onClick = { showDeleteDialog = it.id }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
            )
        },
        reorderItem = { moveId, beforeId ->
            editInstrumentViewModel.reorderProcessors(moveId, beforeId)
        },
    )

    if (showAddDialog) {
        ListSelection(
            listItems = pluginRegistry.effectLabels,
            titleText = "Add audio effect",
            onCancel = {
                showAddDialog = false
            },
            onConfirm = { index ->
                val type = pluginRegistry.audioEffects[index].type
                editInstrumentViewModel.createProcessor(type)
                showAddDialog = false
            })
    }

    if (showDeleteDialog != 0L) {
        ConfirmDialog(
            titleText = "Delete Audio FX",
            questionText = "Really delete this audio effect?",
            onFinished = { answer ->
                if (answer) {
                    editInstrumentViewModel.deleteProcessor(showDeleteDialog)
                }
                showDeleteDialog = 0
            }
        )
    }
}

@Composable
fun ModulatorList(
    currentModule: ModuleRef,
    onSelectModule: (ModuleRef) -> Unit,
    editInstrumentViewModel: EditInstrumentViewModel = viewModel(),
    pluginRegistry: de.nullgrad.pocketband.plugins.PlugInRegistry = remember { LOCATOR.get<de.nullgrad.pocketband.plugins.PlugInRegistry>() }
) {
    var showDeleteDialog by remember { mutableLongStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    val editInstrumentState by editInstrumentViewModel.state.collectAsState()

    EffectRack(
        title = {
            EffectRackTitle(text = "Mods",
                action = {
                    SmallOutlinedAppButton(onClick = { showAddDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        list = editInstrumentState.modulators,
        renderItem = { entry ->
            val interactionSource = remember { MutableInteractionSource() }
            val modifier =
                if (editInstrumentState.modulators.size > 1)
                    Modifier.longPressDraggableHandle(interactionSource = interactionSource)
                else Modifier
            val selected = currentModule == entry.module
            val scale by animateFloatAsState(targetValue = if (selected) 1.1f else 1f, label = "scale")
            ModuleEffectSlot(
                modifier = modifier.tint(Theme.colors.controlsAccentTint, enabled = selected)
                    .scale(scale),
                module = entry.module,
                isMuted = entry.mute.asBool,
                onChangeMuted = { newState ->
                    editInstrumentViewModel.setMute(entry.module, newState)
                },
                onSelectModule = onSelectModule,
                specialAction = {
                    AppIconButton(onClick = { showDeleteDialog = it.id }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
            )
        },
        reorderItem = { moveId, beforeId ->
            editInstrumentViewModel.reorderProcessors(moveId, beforeId)
        },
    )

    if (showAddDialog) {
        ListSelection(
            listItems = pluginRegistry.modulatorLabels,
            titleText = "Add Modulator",
            onCancel = {
                showAddDialog = false
            },
            onConfirm = { index ->
                val type = pluginRegistry.modulators[index].type
                editInstrumentViewModel.createProcessor(type)
                showAddDialog = false
            })
    }

    if (showDeleteDialog != 0L) {
        ConfirmDialog(
            titleText = "Delete Modulator",
            questionText = "Really delete this Modulator?",
            onFinished = { answer ->
                if (answer) {
                    editInstrumentViewModel.deleteProcessor(showDeleteDialog)
                }
                showDeleteDialog = 0
            }
        )
    }
}
