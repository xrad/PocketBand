package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import de.nullgrad.pocketband.ui.theme.ShapingDefaults
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun Tinted(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Theme.colors.controlsAccent.copy(alpha = 0.2f),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.tint(color, enabled = enabled)) {
        content()
    }
}

fun Modifier.tint(
    color: Color,
    cornerRadius: Int = ShapingDefaults.MEDIUM,
    enabled: Boolean = true,
) = this.drawWithContent {
    drawContent()
    if (enabled) {
        drawRoundRect(color,
            size = size,
            cornerRadius = CornerRadius(cornerRadius * density)
        )
    }
}
