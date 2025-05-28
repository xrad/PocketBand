package de.nullgrad.pocketband.ui.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(modifier = modifier,
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = Theme.fonts.labelSmall)
}