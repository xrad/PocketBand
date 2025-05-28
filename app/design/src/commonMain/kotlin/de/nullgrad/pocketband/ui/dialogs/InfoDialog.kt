package de.nullgrad.pocketband.ui.dialogs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun ConfirmDialog(
    titleText: String,
    questionText: String,
    onFinished: (result: Boolean) -> Unit,
) {
    AppDialog(
        title = titleText,
        onDismiss = { onFinished(false) },
        onConfirm = { onFinished(true) },
        content = {
            Text(text = questionText, style = Theme.fonts.dialogContent)
        },
    )
}