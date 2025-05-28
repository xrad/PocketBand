package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.nullgrad.pocketband.ui.theme.LocalContainerColor
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun Panel(
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.panelColor,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) {
    Card(
        shape = Theme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor,
            contentColor = contentColorFor(containerColor)
        ),
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(Theme.spacing.normal),
            contentAlignment = contentAlignment
        ) {
            CompositionLocalProvider(
                LocalContainerColor provides containerColor,
                content = content)
        }
    }
}
