package de.nullgrad.pocketband.utils

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.nullgrad.pocketband.ui.widgets.AppToggleButton
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AppWidgetsKtTest {

    @Test
    fun testAppToggleButton() = runComposeUiTest {
        var state = false
        setContent {
            AppToggleButton(
                selected = state,
                onSelectedChange = {
                    state = it
                }) {
                Text("Press")
            }
        }
        assertEquals(false, state)
        onNodeWithText("Press").performClick()
        assertEquals(true, state)

    }

}