package de.nullgrad.pocketband.edit.uimodel

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.nullPlugin
import kotlin.math.roundToInt

@Immutable
data class ModuleRef(
    val id: Long,
    val kind: PlugInKind,
    val type: String,
    val label: String,
)  {
    companion object {
        val UNDEFINED = ModuleRef(nullPlugin.id, nullPlugin.kind, nullPlugin.type, nullPlugin.label)
    }
}

@Immutable
data class ParameterRef(
    val owner: ModuleRef,
    val key: String,
) {
    val pluginId get() = owner.id
}

@Immutable
data class ParameterValue(
    val parameter: ParameterRef,
    val normalizedValue: Double,
    val effectiveNormalizedValue: Double?,
    val value: Double,
    val valueStr: String,
) {
    val asBool: Boolean
        get() = value >= 0.5

    val asInt: Int
        get() = value.roundToInt()
}

@Immutable
data class ParameterModulation(
    val id: Int,
    val source: ParameterRef,
    val target: ParameterRef,
    val amount: Double,
)
