package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun <T> AppDropDownSelection(
    items: List<T>,
    itemContent: @Composable (item: T) -> Unit,
    selectedIndex: Int,
    onSelectItem: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPopupMenu by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier.clickable { showPopupMenu = true }) {
        if (selectedIndex in items.indices) {
            itemContent(items[selectedIndex])
        }
        DropdownMenu(
            expanded = showPopupMenu,
            onDismissRequest = { showPopupMenu = false }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        showPopupMenu = false
                        onSelectItem(index)
                    },
                    text = {
                        itemContent(item)
                    }
                )
            }
        }
    }
}
