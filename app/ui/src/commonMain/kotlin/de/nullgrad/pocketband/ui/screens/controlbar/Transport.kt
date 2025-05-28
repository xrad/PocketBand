package de.nullgrad.pocketband.ui.screens.controlbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.ui.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.Locale

@Composable
fun Transport(
    playing: Boolean,
    onStartEngine: () -> Unit,
    onStopEngine: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable {
            if (playing) {
                onStopEngine()
            }
            else {
                onStartEngine()
            }
        },
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.normal)) {
        AudioTime()
    }
}

class TransportViewModel : ViewModel() {
    private val liveEventsService = LOCATOR.get<de.nullgrad.pocketband.liveevents.LiveEventsService>()

    val liveEvents = liveEventsService.transportFlow
}

private const val MILLISECONDS_PER_MINUTE = 60 * 1000
private const val MILLISECONDS_PER_SECOND = 1000

@Composable
fun AudioTime(
    modifier: Modifier = Modifier,
    viewModel: TransportViewModel = viewModel()
) {
    val transport by viewModel.liveEvents.collectAsState(de.nullgrad.pocketband.liveevents.model.TransportUpdate())

    ShowAudioTime(
        milliseconds = transport.milliseconds,
        measure = transport.measure,
        beat = transport.beat,
        tick = transport.tick,
        modifier = modifier,
    )
}

@Composable
private fun ShowAudioTime(
    milliseconds: Int,
    measure: Int,
    beat: Int,
    tick: Int,
    modifier: Modifier = Modifier,
) {
    val minutes : Int = milliseconds / MILLISECONDS_PER_MINUTE
    val rest = milliseconds - minutes * MILLISECONDS_PER_MINUTE
    val seconds : Int = rest / MILLISECONDS_PER_SECOND
    val millis = rest - seconds * MILLISECONDS_PER_SECOND

    Column(modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.normal)) {
        Text(
            text = String.format(Locale.ROOT, "%02d:%02d:%03d",
                minutes, seconds, millis),
            style = Theme.fonts.displayTimePosition,
        )
        Text(
            text = String.format(Locale.ROOT, "%02d:%02d:%03d",
                measure, beat, tick),
            style = Theme.fonts.displayTimeSignature,
        )
    }
}

@Preview
@Composable
private fun AudioTimePreview() {
    Theme {
        ShowAudioTime(
            milliseconds = 1 * 60 * 1000 + 2 * 1000 + 345,
            measure = 1,
            beat = 2,
            tick = 3,
        )
    }
}