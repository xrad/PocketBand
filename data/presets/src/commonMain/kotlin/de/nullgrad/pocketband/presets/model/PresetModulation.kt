package de.nullgrad.pocketband.presets.model

import androidx.compose.runtime.Immutable

@Immutable
data class PresetModulation(
    val sourceModuleId: Long,
    val sourceParameterKey: String,
    val targetModuleId: Long,
    val targetParameterKey: String,
    val amount: String,
)