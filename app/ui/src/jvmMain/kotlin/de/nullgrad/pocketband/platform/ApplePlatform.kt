package de.nullgrad.pocketband.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import de.nullgrad.pocketband.macos.MacOS

internal object ApplePlatform : Platform {

    @Composable
    override fun RequireRecordingPermissions(
        askUser: @Composable (onFinished: (result: Boolean) -> Unit) -> Unit,
        content: @Composable () -> Unit,
    ) {
        val status = MacOS.getMicrophonePermissionStatus()
        println("status: $status")
        if (status == 2) {
            LaunchedEffect(Unit) {
                MacOS.requestMicrophonePermission()
            }
        }
        else {
            content()
        }
    }
}

actual fun getPlatform(): Platform = ApplePlatform

