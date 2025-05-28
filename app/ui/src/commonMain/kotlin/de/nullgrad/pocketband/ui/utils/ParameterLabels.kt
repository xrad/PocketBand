package de.nullgrad.pocketband.ui.utils

import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.ui.plugins.getParameterLabel

internal fun buildParameterLabel(paramRef: ParameterRef): String {
    val keyLabel = getParameterLabel(paramRef.owner.type, paramRef.key)
    return "${paramRef.owner.label}/$keyLabel"
}