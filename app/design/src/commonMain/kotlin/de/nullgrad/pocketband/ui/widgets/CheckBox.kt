package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun CheckBox(
    checked: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val colors = Theme.colors
        val fillColor = if (checked) colors.controls else Color.Transparent
        Box(modifier = Modifier
            .size(20.dp)
            .background(color = fillColor)
            .border(
                width = 2.dp,
                color = colors.controls
            )
            .clickable {
                onCheckedChange(!checked)
            }
        )
        Spacer(Modifier.width(Theme.spacing.normal))
        Text(modifier = modifier,
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = Theme.fonts.labelSmall)
    }
}