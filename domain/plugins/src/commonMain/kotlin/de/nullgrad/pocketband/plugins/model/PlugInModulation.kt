package de.nullgrad.pocketband.plugins.model

import androidx.compose.runtime.Immutable

@Immutable
data class PlugInModulation(
    val source: PlugInParameter,
    val target: PlugInParameter,
    val amount: Double,
)