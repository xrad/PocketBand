package de.nullgrad.pocketband.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import de.nullgrad.pocketband.ui.theme.Theme

@Composable
fun ListSelection(listItems: List<String>,
                  titleText: String,
                  onCancel: () -> Unit,
                  onConfirm: (index: Int) -> Unit) {
    AppDialog(
        onDismiss = onCancel,
        title = titleText,
        content = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                ) {
                listItems.forEachIndexed { index, item ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Theme.spacing.normal)
                        .clickable { onConfirm(index) }
                    ) {
                        Text(modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = Theme.fonts.selectionItem,
                            text = item)
                    }
                }
            }
        }
    )
}
