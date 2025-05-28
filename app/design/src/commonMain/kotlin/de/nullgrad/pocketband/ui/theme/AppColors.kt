package de.nullgrad.pocketband.ui.theme

import androidx.annotation.IntRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Immutable
data class AppColors(
    val background: Color = Color.Gray,
    val controls: Color = Color.White,
    val controlsDim: Color = Color.White.copy(.2f),
    val controlsOverlay: Color = Color.Yellow,
    val controlsAccent: Color = Color.White,
    val controlsAccentTint: Color = Color.White.copy(0.2f),
    val controlsRecord: Color = Color.Red,
    val padColor: Color = Color.White,
    val padColorContrast: Color = Color.Black,
    val padColorTonic: Color = Color.Gray,
    val padColorAccent: Color = Color.Green,
    val padLabel: Color = Color.Yellow,
    val panelColor: Color = Color.Blue,
    val onPanelColor: Color = Color.White,
    val panelColorAlt: Color = Color.Green,
    val panelColorAltSelected: Color = Color.Yellow,
)

val LocalAppColors = staticCompositionLocalOf { AppColors() }

private fun Color.flipLuminosity(): Color {
    val hsl = FloatArray(3)
    rgbToHsl((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), hsl)
    hsl[2] = when {
        hsl[2] > 0.7f -> 1f - hsl[2] * 0.5f  // For very bright colors, reduce lightness
        hsl[2] < 0.3f -> 1f - hsl[2] * 1.5f  // For very dark colors, increase lightness
        else -> 1f - hsl[2]  // For medium lightness, flip it harmonically
    }
    return Color.hsl(hsl[0], hsl[1], hsl[2], alpha)
}

@Composable
fun getAppColors() : AppColors {
    return AppColors(
        background = MaterialTheme.colorScheme.surface,
        controls = MaterialTheme.colorScheme.secondary,
        controlsDim = MaterialTheme.colorScheme.secondary.copy(alpha = .4f),
        controlsOverlay = MaterialTheme.colorScheme.onSecondary,
        controlsAccent = MaterialTheme.colorScheme.primary,
        controlsAccentTint = MaterialTheme.colorScheme.primary.copy(alpha = .2f),
        controlsRecord = Color.Red,
        padColor = MaterialTheme.colorScheme.secondary,
        padColorContrast = MaterialTheme.colorScheme.secondary.flipLuminosity(),
        padColorTonic = MaterialTheme.colorScheme.primary,
        padColorAccent = MaterialTheme.colorScheme.tertiary,
        padLabel = MaterialTheme.colorScheme.onSecondary,
        panelColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        onPanelColor = MaterialTheme.colorScheme.onSurface,
        panelColorAlt = MaterialTheme.colorScheme.surfaceContainerLowest,
        panelColorAltSelected = MaterialTheme.colorScheme.tertiaryContainer,
    )
}

private fun rgbToHsl(
    @IntRange(from = 0x0, to = 0xFF) r: Int,
    @IntRange(from = 0x0, to = 0xFF) g: Int, @IntRange(from = 0x0, to = 0xFF) b: Int,
    outHsl: FloatArray
) {
    val rf = r / 255f
    val gf = g / 255f
    val bf = b / 255f

    val max =
        max(rf.toDouble(), max(gf.toDouble(), bf.toDouble())).toFloat()
    val min =
        min(rf.toDouble(), min(gf.toDouble(), bf.toDouble())).toFloat()
    val deltaMaxMin = max - min

    var h: Float
    val s: Float
    val l = (max + min) / 2f

    if (max == min) {
        // Monochromatic
        s = 0f
        h = s
    } else {
        h = if (max == rf) {
            ((gf - bf) / deltaMaxMin) % 6f
        } else if (max == gf) {
            ((bf - rf) / deltaMaxMin) + 2f
        } else {
            ((rf - gf) / deltaMaxMin) + 4f
        }

        s = (deltaMaxMin / (1f - abs((2f * l - 1f).toDouble()))).toFloat()
    }

    h = (h * 60f) % 360f
    if (h < 0) {
        h += 360f
    }

    outHsl[0] = h.coerceIn(0f, 360f)
    outHsl[1] = s.coerceIn(0f, 1f)
    outHsl[2] = l.coerceIn(0f, 1f)
}
