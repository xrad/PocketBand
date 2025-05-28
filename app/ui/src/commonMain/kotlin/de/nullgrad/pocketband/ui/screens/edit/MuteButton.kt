package de.nullgrad.pocketband.ui.screens.edit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.ui.widgets.AppIconButton

@Composable
fun MuteButton(
    isMuted: Boolean,
    onChangeMuted: (Boolean) -> Unit,
) {
    val icon = if (isMuted)
        Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp
    AppIconButton(onClick = {
        onChangeMuted(!isMuted)
    }) {
        Icon(imageVector = icon, contentDescription = "Mute")
    }
}