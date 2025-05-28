package de.nullgrad.pocketband.ui.screens.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import de.nullgrad.pocketband.edit.uimodel.ModuleRef

@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
) {
    val currentModule by rememberCurrentModule()

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
        Row(modifier = modifier
            .verticalScroll(state = rememberScrollState())
        ) {
            EditInstrument(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.weight(1f)) {
                if (currentModule != ModuleRef.UNDEFINED) {
                    PluginController(currentModule)
                }
            }
        }
    }
    else {
        Column(modifier = modifier
            .verticalScroll(state = rememberScrollState())
        ) {
            EditInstrument()
            if (currentModule != ModuleRef.UNDEFINED) {
                PluginController(currentModule)
            }
        }
    }
}

