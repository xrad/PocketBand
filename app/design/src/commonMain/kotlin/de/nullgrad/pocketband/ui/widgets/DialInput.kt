package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun DialInput(
    value: Float,
    onValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onResetValue: () -> Unit = { }
) {
    val color = Theme.colors.controls
    val colorDim = Theme.colors.controlsDim
    SwipeInput(
        initialValue = value,
        modifier = modifier.padding(2.dp),
        onValueChanged = onValueChanged,
        onResetValue = onResetValue,
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
                .padding(2.dp)
        ) {
            val arcRange = 270f
            val startAngle = -arcRange + 45
            val sweepAngle = arcRange * value
            drawArc(
                topLeft = Offset(0f, 0f),
                size = size,
                color = colorDim,
                startAngle = startAngle + sweepAngle,
                sweepAngle = arcRange - sweepAngle,
                useCenter = false,
                style = Stroke(width = 2*density)
            )
            drawArc(
                topLeft = Offset(0f, 0f),
                size = size,
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 6*density)
            )
        }
    }
}

@Preview
@Composable
fun DialInputPreview() {
    var value by remember { mutableFloatStateOf(0.2f) }
    DialInput(
        modifier = Modifier.width(100.dp).height(100.dp),
        value = value,
        onValueChanged = { value = it },
        onResetValue = { value = 0.5f },
    )
}