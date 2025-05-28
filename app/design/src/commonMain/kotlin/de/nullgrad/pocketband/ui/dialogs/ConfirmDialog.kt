package de.nullgrad.pocketband.ui.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun InfoDialog(
    titleText: String,
    infoText: String,
    onFinished: () -> Unit,
) {
    AppDialog(
        title = titleText,
        dismiss = {
            Spacer(Modifier)
        },
        onConfirm = { onFinished() },
        content = {
            Text(text = infoText, style = Theme.fonts.dialogContent)
        },
    )
}