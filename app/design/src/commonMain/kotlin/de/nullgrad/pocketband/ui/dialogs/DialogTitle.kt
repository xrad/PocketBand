package de.nullgrad.pocketband.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun DialogTitle(text: String) {
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center) {
        Text(modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = text,
            style = Theme.fonts.dialogTitle)
    }
}