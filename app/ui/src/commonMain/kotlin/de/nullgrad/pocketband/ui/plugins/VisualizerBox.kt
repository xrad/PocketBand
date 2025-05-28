package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun VisualizerBox(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    content: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .padding(Theme.spacing.rows)
            .background(background, shape = Theme.shapes.medium)
            .border(
                width = 2.dp,
                color = LocalContentColor.current,
                shape = Theme.shapes.medium,
            )
            .padding(Theme.spacing.rows)
    ) {
        content?.invoke()
    }
}