package de.nullgrad.pocketband.ui.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun TouchPad(
    onTapDown: () -> Unit,
    onTapUp: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.padColor,
    pressedColor: Color = Theme.colors.padColorAccent,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val animatedColor: Color by animateColorAsState(
        label = "padColor",
        targetValue = if (pressed) pressedColor else color,
    )
    val scale by animateFloatAsState(targetValue = if (pressed) 1.1f else 1f, label = "scale")
    Card(
        shape = Theme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        modifier = modifier
            .scale(scale = scale)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val type = event.type
                        if (type == PointerEventType.Press) {
                            onTapDown()
                            pressed = true
                        }
                        if (type == PointerEventType.Release) {
                            onTapUp()
                            pressed = false
                        }
                    }
                }
            }
    ) {
        Box(contentAlignment = contentAlignment,
            modifier = Modifier.fillMaxSize(),
        ) {
            content()
        }
    }
}


@Preview
@Composable
private fun KeyboardPadPreview() {
    Theme {
        Box(modifier = Modifier.size(100.dp)) {
            TouchPad(
                modifier = Modifier.fillMaxSize(),
                onTapUp = { },
                onTapDown = { }
            ) {
                Text("Touch",
                    modifier = Modifier.wrapContentSize(),
                )
            }
        }
    }
}