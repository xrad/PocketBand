package de.nullgrad.pocketband.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import de.nullgrad.pocketband.design.generated.resources.Outfit_VariableFont_wght
import de.nullgrad.pocketband.design.generated.resources.Res

data class AppFonts(
    val patchName: TextStyle = TextStyle.Default,
    val displayTimePosition: TextStyle = TextStyle.Default,
    val displayTimeSignature: TextStyle = TextStyle.Default,
    val displayTempo: TextStyle = TextStyle.Default,
    val dialogTitle: TextStyle = TextStyle.Default,
    val dialogContent: TextStyle = TextStyle.Default,
    val dialogHeading: TextStyle = TextStyle.Default,
    val selectionItem: TextStyle = TextStyle.Default,
    val labelSmall: TextStyle = TextStyle.Default,
    val effectSlotLabel: TextStyle = TextStyle.Default,
    val effectRackTitle: TextStyle = TextStyle.Default,
    val button: TextStyle = TextStyle.Default,
    val keyPad: TextStyle = TextStyle.Default,
)

val LocalAppFonts = staticCompositionLocalOf {
    AppFonts()
}

@Composable
fun getAppFonts() : AppFonts {
    // avoid custom font for @Previews
    if (LocalInspectionMode.current) return AppFonts()
    val outfitNormal = Font(Res.font.Outfit_VariableFont_wght, style = FontStyle.Normal)
    val outfitMedium = Font(Res.font.Outfit_VariableFont_wght, style = FontStyle.Normal,
        weight = FontWeight.Medium)
    val outfitSemiBold = Font(Res.font.Outfit_VariableFont_wght, style = FontStyle.Normal,
        weight = FontWeight.SemiBold)

    return AppFonts(
        patchName = Theme.typography.titleMedium.copy(
            fontFamily = FontFamily(outfitNormal),
            fontFeatureSettings = "smcp"),
        displayTimePosition = Theme.typography.labelSmall.copy(
            fontFamily = FontFamily(outfitMedium),
            fontFeatureSettings = "tnum"),
        displayTimeSignature = Theme.typography.labelSmall.copy(
            fontFamily = FontFamily(outfitMedium),
            fontFeatureSettings = "tnum"),
        displayTempo = Theme.typography.labelSmall.copy(
            fontFamily = FontFamily(outfitSemiBold),
            fontFeatureSettings = "tnum"),
        dialogTitle = Theme.typography.headlineSmall.copy(fontFamily = FontFamily(outfitNormal)),
        dialogContent = Theme.typography.bodyLarge.copy(fontFamily = FontFamily(outfitNormal)),
        dialogHeading = Theme.typography.titleMedium.copy(fontFamily = FontFamily(outfitNormal)),
        selectionItem = Theme.typography.bodyLarge.copy(fontFamily = FontFamily(outfitNormal)),
        labelSmall = Theme.typography.labelSmall.copy(fontFamily = FontFamily(outfitNormal)),
        effectSlotLabel = Theme.typography.labelMedium.copy(fontFamily = FontFamily(outfitNormal)),
        effectRackTitle = Theme.typography.titleSmall.copy(fontFamily = FontFamily(outfitNormal)),
        button = Theme.typography.bodyMedium.copy(fontFamily = FontFamily(outfitNormal)),
        keyPad = Theme.typography.bodyMedium.copy(fontFamily = FontFamily(outfitSemiBold)),
    )
}

