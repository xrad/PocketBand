package de.nullgrad.pocketband.plugins.model

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.presets.model.PresetParameter

@Immutable
data class PluginDescriptor(
    val type: String,
    val label: String,
    val kind: PlugInKind,
    val createPlugin: suspend (id: Long, parameters: List<PresetParameter>) -> PlugIn
)