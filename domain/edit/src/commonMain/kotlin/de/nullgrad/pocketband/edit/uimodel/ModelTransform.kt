package de.nullgrad.pocketband.edit.uimodel

import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInModulation
import de.nullgrad.pocketband.plugins.model.PlugInParameter

// Assuming PlugIn has properties id, label, kind, and type
fun PlugIn.toModuleRef(): ModuleRef {
    return ModuleRef(
        id = id,
        label = label,
        kind = kind,
        type = type,
    )
}

fun PlugInParameter.toParameterRef(): ParameterRef {
    return ParameterRef(
        owner = owner.toModuleRef(),
        key = key,
    )
}

fun PlugInParameter.toParameterValue(): ParameterValue {
    return ParameterValue(
        parameter = toParameterRef(),
        normalizedValue = normalizedValue,
        effectiveNormalizedValue = if (isModulated) effectiveNormalizedValue else null,
        value = value,
        valueStr = valueStr,
    )
}

fun PlugInModulation.toParameterModulation(id: Int): ParameterModulation {
    return ParameterModulation(
        id = id,
        source = source.toParameterRef(),
        target = target.toParameterRef(),
        amount = amount,
    )
}
