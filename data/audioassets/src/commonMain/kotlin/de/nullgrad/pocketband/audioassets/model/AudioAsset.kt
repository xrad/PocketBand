package de.nullgrad.pocketband.audioassets.model

import androidx.compose.runtime.Immutable

@Immutable
data class AudioAsset(
    val path: String,
    val isBuiltIn: Boolean,
    val lastModified: Long,
) {
    val label: String
        get() = path.substringAfterLast("/") // Safer alternative to basename
}
