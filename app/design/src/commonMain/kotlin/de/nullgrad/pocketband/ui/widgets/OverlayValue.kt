package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import de.nullgrad.pocketband.ui.theme.Theme

private const val thickness = 18f

@Composable
fun OverlayValue(
    value: Double?,
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.controlsOverlay,
    content: @Composable () -> Unit,
) {

    Box(modifier = modifier
        .drawWithContent {
            drawContent()
            if (value != null) {
                drawRect(color,
                    topLeft = Offset(10f, (size.height - thickness) / 2),
                    size = Size(
                        value.toFloat() * (size.width - 20f),
                        thickness
                    )
                )
            }
        }) {
        content()
    }
}