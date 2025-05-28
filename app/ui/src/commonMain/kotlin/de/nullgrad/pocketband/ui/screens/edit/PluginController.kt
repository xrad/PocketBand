package de.nullgrad.pocketband.ui.screens.edit

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.ui.plugins.CreatePluginUi

@Composable
fun PluginController(module: ModuleRef) {
    module.CreatePluginUi()
}