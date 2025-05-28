package de.nullgrad.pocketband.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalContainerColor = compositionLocalOf { Color.Black }

@Composable
fun Theme(
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme()
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = getShapes(),
        typography = getTypography(),
    ) {
        val appColors = getAppColors()
        CompositionLocalProvider(
            LocalContainerColor provides appColors.background,
            LocalAppSpacing provides getAppSpacing(),
            LocalAppColors provides appColors,
            LocalAppFonts provides getAppFonts(),
            content = content
        )
    }
}

object Theme {
    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val fonts: AppFonts
        @Composable
        @ReadOnlyComposable
        get() = LocalAppFonts.current

    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val spacing: AppSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current
}