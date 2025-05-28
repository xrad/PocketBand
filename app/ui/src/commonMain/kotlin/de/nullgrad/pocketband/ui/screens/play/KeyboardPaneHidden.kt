package de.nullgrad.pocketband.ui.screens.play

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun KeyboardPaneHidden(
    keyboardStyleSelector: KeyboardStyleSelectionComposable,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        keyboardStyleSelector(Modifier)
    }
}