package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Labelled(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
 ) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Label(label)
        content()
    }
}