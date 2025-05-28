package de.nullgrad.pocketband.ui.screens.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.Panel
import de.nullgrad.pocketband.ui.widgets.Tinted
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableScope

@Composable
fun EffectRack(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    list: List<ModuleEntry>,
    renderItem: @Composable ReorderableScope.(ModuleEntry) -> Unit,
    reorderItem: ((Long, Long) -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Panel(
        modifier = Modifier.fillMaxWidth()
            .padding(all = Theme.spacing.small),
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            title()
            ReorderableColumn(list = list, onSettle = { from, to ->
                if (reorderItem != null) {
                    val moveId = list[from].module.id
                    val beforeId = if (to == list.size - 1) {
                        ModuleRef.UNDEFINED.id
                    } else list[to].module.id
                    reorderItem.invoke(moveId, beforeId)
                }
            }) {
                _, item, isDragging ->
                Tinted(enabled = isDragging) {
                    renderItem(item)
                }
            }
            if (action != null) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    action()
                }
            }
            else {
                Spacer(modifier = Modifier.height(Theme.spacing.normal))
            }
        }
    }
}

@Composable
fun ModuleEffectSlot(
    module: ModuleRef,
    isMuted: Boolean,
    onChangeMuted: (Boolean) -> Unit,
    onSelectModule: (ModuleRef) -> Unit,
    specialAction: @Composable ((ModuleRef) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    EffectSlot(
        modifier = modifier,
        onTap = {
            onSelectModule(module)
        },
        leadingExtra = {
            MuteButton(isMuted, onChangeMuted = onChangeMuted)
        },
        trailingExtra = {
            specialAction?.invoke(module)
        },
    ) {
        EffectSlotLabel(text = module.label)
    }
}

@Composable
private fun EffectSlot(
    modifier: Modifier = Modifier,
    onTap: () -> Unit,
    leadingExtra: @Composable (() -> Unit)?,
    trailingExtra: @Composable (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingExtra?.invoke()
        Panel(
            containerColor = Theme.colors.panelColorAlt,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onTap)
        ) {
            content()
        }
        trailingExtra?.invoke()
    }
}

@Composable
fun EffectRackTitle(
    modifier: Modifier = Modifier,
    text: String,
    action: (@Composable () -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = Theme.fonts.effectRackTitle)
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            action?.invoke()
        }
    }
}

@Composable
fun EffectSlotLabel(
    modifier: Modifier = Modifier,
    text: String) {
    Text(modifier = modifier,
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = Theme.fonts.effectSlotLabel)
}
