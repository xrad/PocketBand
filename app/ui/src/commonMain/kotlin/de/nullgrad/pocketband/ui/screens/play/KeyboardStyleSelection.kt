package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.ui.keyboard.KeyboardStyle
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.AppIconButton
import org.jetbrains.compose.resources.painterResource
import de.nullgrad.pocketband.design.generaded.resources.Res
import de.nullgrad.pocketband.design.generaded.resources.piano

@Composable
fun KeyboardStyleSelection(
    keyboardStyle: KeyboardStyle,
    onStyleChange: (KeyboardStyle) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPopupMenu by remember { mutableStateOf(false) }

    val items = KeyboardStyle.entries.filter { it != KeyboardStyle.None }

    Box {
        AppIconButton(
            modifier = modifier,
            onLongClick = {
                val index = (items.indexOf(keyboardStyle) + 1) % items.size
                onStyleChange(items[index])
            },
            onClick = {
                showPopupMenu = true
            }
        ) {
            Icon(painterResource(Res.drawable.piano), contentDescription = "Keyboard Style")
        }
        DropdownMenu(
            expanded = showPopupMenu,
            onDismissRequest = { showPopupMenu = false }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        showPopupMenu = false
                        onStyleChange(items[index])
                    },
                    text = {
                        Text(item.name, style = Theme.fonts.labelSmall)
                    }
                )
            }
        }
    }
}
