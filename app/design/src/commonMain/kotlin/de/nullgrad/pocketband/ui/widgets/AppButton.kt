package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.nullgrad.pocketband.ui.theme.Theme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {},
    enabled: Boolean = true,
    padding: PaddingValues = PaddingValues(Theme.spacing.button),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val contentColor = if (enabled) Theme.colors.controls else Theme.colors.controlsDim

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(Theme.shapes.medium)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(bounded = false)
            )
            .padding(padding),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides Theme.fonts.button,
            content = content
        )
    }
}


@Composable
fun SmallAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    AppButton(
        onClick = onClick,
        modifier = modifier,
        padding = PaddingValues(Theme.spacing.small),
        enabled = enabled,
        content = content,
    )
}

@Composable
fun OutlinedAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    AppButton(
        onClick = onClick,
        modifier = modifier.border(width = 1.dp,
            color = Theme.colors.controls, shape = Theme.shapes.medium),
        enabled = enabled,
        content = content,
    )
}


@Composable
fun SmallOutlinedAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    SmallAppButton(
        onClick = onClick,
        modifier = modifier.border(width = 1.dp,
            color = Theme.colors.controls, shape = Theme.shapes.medium),
        enabled = enabled,
        content = content,
    )
}

