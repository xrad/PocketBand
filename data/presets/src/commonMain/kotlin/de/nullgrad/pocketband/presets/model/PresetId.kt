package de.nullgrad.pocketband.presets.model

import androidx.compose.runtime.Immutable

@Immutable
data class PresetId(
    val id: Int,
    val name: String,
)