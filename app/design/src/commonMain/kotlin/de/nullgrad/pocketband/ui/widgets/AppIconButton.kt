package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {},
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    size: Dp = 32.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            //.minimumInteractiveComponentSize()
            .size(size)
            .clip(CircleShape)
            .background(color = Color.Transparent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentColor = if (enabled) Theme.colors.controls else Theme.colors.controlsDim
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}