package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun ParameterRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            Theme.spacing.rows,
            alignment = Alignment.CenterHorizontally
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        content()
    }
}