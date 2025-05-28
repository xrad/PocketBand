package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.LocalContainerColor
import de.nullgrad.pocketband.ui.theme.Theme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppToggleButton(
    selected: Boolean,
    onSelectedChange: (selected: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val color: Color
    val contentColor: Color
    if (selected) {
        color = Theme.colors.controls
        contentColor = LocalContainerColor.current
    }
    else {
        contentColor = Theme.colors.controls
        color = LocalContainerColor.current
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(Theme.shapes.large)
            .background(color)
            .border(width = 1.dp, color = Theme.colors.controls, shape = Theme.shapes.large)
            .combinedClickable(
                onClick = {
                    onSelectedChange(!selected)
                },
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = ripple(bounded = false)
            )
            .padding(Theme.spacing.small),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            content = content
        )
    }
}