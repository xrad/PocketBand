package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import de.nullgrad.pocketband.presets.model.PresetId
import de.nullgrad.pocketband.ui.dialogs.AppDialog
import de.nullgrad.pocketband.ui.screens.manage.PresetList
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun SaveAsDialog(
    defaultId: Int,
    defaultName: String,
    presets: List<PresetId>,
    onSave: (id: Int, name: String) -> Unit,
    onDismiss: () -> Unit
) {
    var id by remember { mutableIntStateOf(defaultId) }
    var name by remember { mutableStateOf(defaultName) }

    AppDialog(
        title = "Save Preset",
        onDismiss = { onDismiss() },
        onConfirm = { onSave(id, name) },
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.normal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PresetList(presets, id, onSelectPreset = {
                    id = it
                    name = presets[it].name
                })
                TextField(value = name, textStyle = Theme.fonts.patchName,
                    onValueChange = { name = it })
            }
        },
    )
}