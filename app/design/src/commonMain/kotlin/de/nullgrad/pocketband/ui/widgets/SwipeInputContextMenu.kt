package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Popup

@Composable
fun SwipeInputContextMenu(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    menu: @Composable () -> Unit,
    content: @Composable () -> Unit,
    visible: Boolean = true,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .focusable(
                interactionSource = interactionSource
            )
    ) {
        if (visible) {
            Popup(
                onDismissRequest = {
                    interactionSource.tryEmit(SwipeInputInteraction.DismissContextMenu())
                },
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -size.height)) {
                Box(modifier = Modifier.onSizeChanged { size = it }) {
                    menu()
                }
            }
        }
        content()
    }
}