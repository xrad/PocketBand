package de.nullgrad.pocketband.presets.model

import androidx.compose.runtime.Immutable

@Immutable
data class Preset(
    val presetId: PresetId,
    val modules: List<PresetModule>,
    val modulations: List<PresetModulation>,
)

val undefinedPreset = Preset(
    presetId = PresetId(-1, name = "Init"),
    modules = emptyList(),
    modulations = emptyList()
)
