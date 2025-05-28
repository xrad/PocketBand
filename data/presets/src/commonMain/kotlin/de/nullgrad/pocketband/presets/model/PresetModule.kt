package de.nullgrad.pocketband.presets.model

import androidx.compose.runtime.Immutable

@Immutable
data class PresetModule(
    val id: Long,
    val type: String,
    val parameters: List<PresetParameter>
)