package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.ui.dialogs.TextEditDialog
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun EditablePatchName(
    presetName: String,
    presetLabel: String,
    onChangeName: (String) -> Unit,
) {
    var editPatch by remember { mutableStateOf(false) }
    if (editPatch) {
        TextEditDialog(
            title = "Change preset name",
            initialValue = presetName,
            onConfirm = { name ->
                onChangeName(name)
                editPatch = false
            },
            onDismiss = { editPatch = false }
        )
    }
    PatchName(presetLabel, onClick = { editPatch = true }, Modifier.fillMaxWidth())
}

@Composable
private fun PatchName(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier
        .clickable {
            onClick()
        }
    ) {
        Text(
            text = label,
            style = Theme.fonts.patchName,
        )
    }
}

