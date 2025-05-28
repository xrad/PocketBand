package de.nullgrad.pocketband.presets.model

import androidx.compose.runtime.Immutable

@Immutable
data class PresetParameter(
    val key: String,
    val value: String,
)