package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.plugins.noteeffects.NoteEcho
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider

object NoteEchoUi : PluginUi {
    override val type = NoteEcho.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        NoteEcho.KEY_DECAY to "Decay",
        NoteEcho.KEY_DELAY to "Delay",
        NoteEcho.KEY_PITCH to "Pitch",
        NoteEcho.KEY_NUM_ECHOES to "# Echos",
    )

    @Composable
    override fun createController() {
        NoteEchoUi()
    }
}

@Composable
fun NoteEchoUi() {
    ParameterColumn {
        ParameterRow {
            ParameterSlider(paramKey = NoteEcho.KEY_DECAY, formatValue = ::percentFormatter)
            ParameterSlider(paramKey = NoteEcho.KEY_DELAY, formatValue = ::noteTypeFormatter)
        }
        ParameterRow {
            ParameterSlider(paramKey = NoteEcho.KEY_PITCH, formatValue = ::semiTonesFormatter)
            ParameterSlider(paramKey = NoteEcho.KEY_NUM_ECHOES, formatValue = ::intFormatter)
        }
    }
}