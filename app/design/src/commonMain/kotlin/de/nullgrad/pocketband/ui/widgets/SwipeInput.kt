package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun SwipeInput(
    initialValue: Float,
    onValueChanged: (Float) -> Unit,
    onResetValue: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    val value = remember { mutableFloatStateOf(initialValue) }

    Box(modifier = modifier
        .swipeInput(
            value.value,
            { v ->
                value.value = v
                onValueChanged(v)
            },
            onResetValue,
            focusRequester = focusRequester,
            interactionSource = interactionSource)
        .focusRequester(focusRequester)
        .focusable(interactionSource = interactionSource)
    ) {
        content()
    }
}



