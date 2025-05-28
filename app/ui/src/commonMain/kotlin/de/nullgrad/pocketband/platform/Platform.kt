package de.nullgrad.pocketband.platform

import androidx.compose.runtime.Composable

interface Platform {
    @Composable
    fun RequireRecordingPermissions(
        askUser: @Composable (onFinished: (result: Boolean) -> Unit) -> Unit,
        content: @Composable () -> Unit,
    )
}

expect fun getPlatform(): Platform