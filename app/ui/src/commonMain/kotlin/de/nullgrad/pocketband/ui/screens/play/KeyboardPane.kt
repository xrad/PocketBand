package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.ui.keyboard.KeyboardStyle
import de.nullgrad.pocketband.ui.keyboard.MidiKeyboardViewModel

typealias KeyboardStyleSelectionComposable = @Composable (modifier: Modifier) -> Unit

@Composable
fun KeyboardPane(
    modifier: Modifier = Modifier,
    viewModel: MidiKeyboardViewModel = viewModel(),
 ) {
    val keyboardState by viewModel.keyboardScaleSteps.collectAsState()
    val keyboardStyle by viewModel.keyboardStyle.collectAsState()

    fun onTapDown(midiKey: MidiKey?) {
        if (midiKey != null) {
            viewModel.keyDown(midiKey)
        }
    }

    fun onTapUp(midiKey: MidiKey?) {
        if (midiKey != null) {
            viewModel.keyUp(midiKey)
        }
    }

    val keyboardStyleSelector: @Composable (modifier: Modifier) -> Unit = {
        KeyboardStyleSelection(
            keyboardStyle = keyboardStyle,
            onStyleChange = { viewModel.setKeyboardStyle(it) },
            modifier = it)
    }

    val duration = 200
    val animSpec = tween<Float>(durationMillis = duration)
    AnimatedContent(
        targetState = keyboardStyle,
        contentAlignment = Alignment.BottomCenter,
        transitionSpec = {
            (fadeIn(animSpec) togetherWith fadeOut(animSpec))
            .using(SizeTransform(
                clip = true,
                sizeAnimationSpec = { _, _ ->
                    tween(durationMillis = duration, easing = LinearOutSlowInEasing)
                }
            ))
        },
        label = "Keyboard Style",
    ) {
        when (it) {
            KeyboardStyle.Hidden -> KeyboardPaneHidden(
                keyboardStyleSelector = keyboardStyleSelector,
                modifier = modifier,
            )
            KeyboardStyle.Small -> KeyboardPaneSmall(
                keyboardStyleSelector = keyboardStyleSelector,
                keyboardState = keyboardState,
                onTapDown = ::onTapDown,
                onTapUp = ::onTapUp,
                modifier = modifier,
            )
            KeyboardStyle.Medium -> KeyboardPaneMedium(
                keyboardStyleSelector = keyboardStyleSelector,
                keyboardState = keyboardState,
                onTapDown = ::onTapDown,
                onTapUp = ::onTapUp,
                modifier = modifier,
            )
            KeyboardStyle.Large -> KeyboardPaneLarge(
                keyboardStyleSelector = keyboardStyleSelector,
                keyboardState = keyboardState,
                onTapDown = ::onTapDown,
                onTapUp = ::onTapUp,
                modifier = modifier,
            )
            KeyboardStyle.None -> Unit
        }
    }
}

