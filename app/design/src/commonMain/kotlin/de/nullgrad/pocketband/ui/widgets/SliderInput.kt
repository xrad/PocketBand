package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SliderInput(
    value: Float,
    onValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.controls,
    colorDim: Color = Theme.colors.controlsDim,
    onResetValue: () -> Unit = { },
    contextMenu: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    SliderInputMenuWrapper(
        interactionSource = interactionSource,
        modifier = modifier,
        contextMenu = contextMenu,
    ) {
        SliderInputImpl(
            value,
            onValueChanged,
            onResetValue = onResetValue,
            color = color,
            colorDim = colorDim,
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun SliderInputMenuWrapper(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    contextMenu: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            when (it) {
                is SwipeInputInteraction.LongPressed -> visible = true
                is SwipeInputInteraction.StartSwiping -> visible = false
                is SwipeInputInteraction.DismissContextMenu -> visible = false
                is FocusInteraction.Unfocus -> visible = false
            }
        }
    }

    if (contextMenu != null) {
        SwipeInputContextMenu(
            modifier = modifier,
            visible = visible,
            interactionSource = interactionSource,
            menu = contextMenu,
            content = content)
    }
    else {
        Box(modifier = modifier) {
            content()
        }
    }
}

@Composable
fun SliderInputImpl(
    value: Float,
    onValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.controls,
    colorDim: Color = Theme.colors.controlsDim,
    onResetValue: () -> Unit = { },
    interactionSource: MutableInteractionSource? = null
) {
    SwipeInput(
        initialValue = value,
        modifier = modifier
            .border(
                width = 2.dp,
                color = color,
                shape = Theme.shapes.medium,
                )
            .padding(2.dp),
        onValueChanged = onValueChanged,
        onResetValue = onResetValue,
        interactionSource = interactionSource,
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val xdiv = size.width * value
            drawRect(color, size = Size(xdiv, size.height))
            drawRect(colorDim, topLeft = Offset(xdiv, 0f),
                size = Size(size.width - xdiv, size.height))
        }
    }
}

@Preview
@Composable
fun SliderInputPreview() {
    var value by remember { mutableFloatStateOf(0.7f) }
    SliderInput(
        modifier = Modifier.width(100.dp).height(25.dp),
        value = value,
        onValueChanged = { value = it },
        onResetValue = { value = 0.5f },
    )
}