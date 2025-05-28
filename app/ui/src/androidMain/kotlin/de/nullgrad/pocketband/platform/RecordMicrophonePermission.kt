package de.nullgrad.pocketband.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun RequireRecordAudioPermissions(
    askUser: @Composable (onFinished: (result: Boolean) -> Unit) -> Unit,
    content: @Composable () -> Unit,
)
{
    val recordAudioPermissionState = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )

    if (!recordAudioPermissionState.status.isGranted) {
        if (recordAudioPermissionState.status.shouldShowRationale) {
            askUser { result ->
                if (result) {
                    recordAudioPermissionState.launchPermissionRequest()
                }
            }
        }
        else {
            LaunchedEffect(Unit) {
                recordAudioPermissionState.launchPermissionRequest()
            }
        }
    }
    else {
        content()
    }
}

