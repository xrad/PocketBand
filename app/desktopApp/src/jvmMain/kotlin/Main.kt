import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import de.nullgrad.pocketband.App

fun main() = singleWindowApplication(
        title = "PocketBand",
        state = WindowState(size = DpSize(500.dp, 800.dp))
    ) {
        App()
    }