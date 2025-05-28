package de.nullgrad.pocketband.ui.dialogs

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun TextEditDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    validator: (String) -> Boolean = { true },
    ) {
    var value by remember {
        mutableStateOf(initialValue)
    }
    AppDialog(
        title = title,
        onConfirm = {
            onConfirm(value)
        },
        onDismiss = onDismiss,
        content = {
            TextField(
                value = value,
                onValueChange = { if (validator(it)) value = it }
            )
        },
    )
}
