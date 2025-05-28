package de.nullgrad.pocketband.platform

import androidx.compose.runtime.Composable

internal object AndroidPlatform : Platform {

    @Composable
    override fun RequireRecordingPermissions(
        askUser: @Composable (onFinished: (result: Boolean) -> Unit) -> Unit,
        content: @Composable () -> Unit,
    ) {
        RequireRecordAudioPermissions(askUser, content)
    }
}

actual fun getPlatform(): Platform = AndroidPlatform

