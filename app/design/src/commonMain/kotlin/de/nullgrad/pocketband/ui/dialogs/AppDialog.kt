package de.nullgrad.pocketband.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun AppDialog(
    title: String? = null,
    onDismiss: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    confirm: (@Composable () -> Unit)? = null,
    dismiss: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
    ) {

    AlertDialog(
        shape = Theme.shapes.medium,
        onDismissRequest = { onDismiss?.invoke() },
        title = {
            if (title != null) {
                DialogTitle(title)
            }
        },
        confirmButton = {
            ProvideTextStyle(Theme.fonts.button) {
                if (confirm != null) confirm.invoke()
                else {
                    TextButton(onClick = { onConfirm?.invoke() }) {
                        Text(text = "OK")
                    }
                }
            }
        },
        dismissButton = {
            ProvideTextStyle(Theme.fonts.button) {
                if (dismiss != null) dismiss.invoke()
                else {
                    TextButton(onClick = { onDismiss?.invoke() }) {
                        Text(text = "Cancel")
                    }
                }
            }
        },
        text = {
            content()
        },
    )
}