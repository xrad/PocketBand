package de.nullgrad.pocketband.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private data object SpacingDefaults {
    const val SMALL = 2
    const val NORMAL = 6
    const val COLUMNS = 8
    const val ROWS = 8
    const val ICONS = 20
    const val BUTTON = 10
}

@Immutable
data class AppSpacing(
    val small: Dp = SpacingDefaults.SMALL.dp,
    val normal: Dp = SpacingDefaults.NORMAL.dp,
    val button: Dp = SpacingDefaults.BUTTON.dp,
    val columns: Dp = SpacingDefaults.COLUMNS.dp,
    val rows: Dp = SpacingDefaults.ROWS.dp,
    val icon: Dp = SpacingDefaults.ICONS.dp
)

val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }

@Composable
fun getAppSpacing() : AppSpacing {
    return AppSpacing()
}