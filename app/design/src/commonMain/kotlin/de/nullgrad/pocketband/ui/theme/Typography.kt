package de.nullgrad.pocketband.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import org.jetbrains.compose.resources.Font
import de.nullgrad.pocketband.design.generated.resources.Outfit_VariableFont_wght
import de.nullgrad.pocketband.design.generated.resources.Res

@Composable
fun getTypography() : Typography {
    val source = MaterialTheme.typography
    // avoid custom font for @Previews
    if (LocalInspectionMode.current) return source
    val outfitNormal = Font(Res.font.Outfit_VariableFont_wght, style = FontStyle.Normal)
    return source.copy(
        displayLarge = source.displayLarge.copy(fontFamily = FontFamily(outfitNormal)),
        displayMedium = source.displayMedium.copy(fontFamily = FontFamily(outfitNormal)),
        displaySmall = source.displaySmall.copy(fontFamily = FontFamily(outfitNormal)),
        headlineLarge = source.headlineLarge.copy(fontFamily = FontFamily(outfitNormal)),
        headlineMedium = source.headlineMedium.copy(fontFamily = FontFamily(outfitNormal)),
        headlineSmall = source.headlineSmall.copy(fontFamily = FontFamily(outfitNormal)),
        titleLarge = source.titleLarge.copy(fontFamily = FontFamily(outfitNormal)),
        titleMedium = source.titleMedium.copy(fontFamily = FontFamily(outfitNormal)),
        titleSmall = source.titleSmall.copy(fontFamily = FontFamily(outfitNormal)),
        bodyLarge = source.bodyLarge.copy(fontFamily = FontFamily(outfitNormal)),
        bodyMedium = source.bodyMedium.copy(fontFamily = FontFamily(outfitNormal)),
        bodySmall = source.bodySmall.copy(fontFamily = FontFamily(outfitNormal)),
        labelLarge = source.labelLarge.copy(fontFamily = FontFamily(outfitNormal)),
        labelMedium = source.labelMedium.copy(fontFamily = FontFamily(outfitNormal)),
        labelSmall = source.labelSmall.copy(fontFamily = FontFamily(outfitNormal)),
    )
}