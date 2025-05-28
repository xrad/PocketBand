package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.midi.MidiSettingsRepository
import de.nullgrad.pocketband.midi.model.midiKeyName
import de.nullgrad.pocketband.plugins.noteeffects.StepSequencer
import de.nullgrad.pocketband.plugins.noteeffects.StepSequencerLiveState
import de.nullgrad.pocketband.plugins.noteeffects.StepSequencerStep
import de.nullgrad.pocketband.ui.screens.edit.EditPluginViewmodel
import de.nullgrad.pocketband.ui.screens.edit.rememberParameterValue
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.DialInput
import de.nullgrad.pocketband.ui.widgets.Panel
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider
import de.nullgrad.pocketband.ui.widgets.Tinted
import kotlinx.coroutines.flow.filterIsInstance

object StepSequencerUi : PluginUi {
    override val type = StepSequencer.PLUGIN_TYPE

    override val parameterLabel = mapOf(
        StepSequencer.KEY_STEP_DURATION to "Step Duration",
        StepSequencer.KEY_OCTAVE to "Octave",
    )

    @Composable
    override fun createController() {
        StepSequencerUi()
    }
}

@Composable
private fun rememberLiveState(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) : State<StepSequencerLiveState> {
    return editPluginViewModel.liveEvents
        .filterIsInstance<StepSequencerLiveState>()
        .collectAsState(initial = StepSequencerLiveState())
}

@Composable
fun StepSequencerUi() {
    val liveState by rememberLiveState()
    ParameterColumn(modifier = Modifier.padding(Theme.spacing.normal)) {
        ParameterRow {
            ParameterSlider(paramKey = StepSequencer.KEY_STEP_DURATION, formatValue = ::noteTypeFormatter)
            ParameterSlider(paramKey = StepSequencer.KEY_OCTAVE, formatValue = ::intFormatter)
        }
        Spacer(modifier = Modifier.height(Theme.spacing.normal))
        StepPanel(liveState.step)
    }
}

@Composable
fun StepPanel(currentStep: Int) {
    Row(modifier = Modifier
        .horizontalScroll(state = rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.normal),
    ) {
        for (index in 0 until StepSequencer.NUM_STEPS) {
            val tintColor = if (index == currentStep)
                Theme.colors.controlsAccent.copy(alpha = 0.5f) else Color.Transparent
            Tinted(color = tintColor) {
                StepWidget(stepIndex = index)
            }
        }
    }
}

@Composable
private fun StepWidget(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
    stepIndex: Int,
) {
    val editPluginState by editPluginViewModel.state.collectAsState()
    val keySignature by LOCATOR.get<MidiSettingsRepository>().keySignature.collectAsState()

    @Immutable
    data class StepParamState(
        val active: ParameterValue,
        val pitch: ParameterValue,
        val duration: ParameterValue,
    )

    val paramState by remember {
        // edit model state can change frequently, so we use derivedStateOf to
        // isolate the changes we care about
        derivedStateOf {
            val pitch = editPluginState.getParamValue("${StepSequencerStep.KEY_PITCH}$stepIndex")
            val active = editPluginState.getParamValue("${StepSequencerStep.KEY_ACTIVE}$stepIndex")
            val duration = editPluginState.getParamValue("${StepSequencerStep.KEY_DURATION}$stepIndex")
            StepParamState(active, pitch, duration)
        }
    }

    Panel {
        ParameterColumn(modifier = Modifier.width(50.dp)) {
            repeat(keySignature.scale.size) { index ->
                val pitchIndex = keySignature.scale.size - index - 1
                val isSelected = paramState.active.asBool && paramState.pitch.asInt == pitchIndex
                StepPad(
                    modifier = Modifier.fillMaxWidth(),
                    pitchIndex = pitchIndex,
                    selected = isSelected,
                    onTap = {
                        editPluginViewModel.setBoolParameter(paramState.active.parameter, !isSelected)
                        editPluginViewModel.setParameter(paramState.pitch.parameter, pitchIndex.toDouble())
                    },
                )
            }
            DialInput(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).fillMaxHeight(),
                value = paramState.duration.value.toFloat(), onValueChanged = {
                    editPluginViewModel.setParameter(paramState.duration.parameter, it.toDouble())
                })
        }
    }
}

@Composable
private fun StepPad(
    modifier: Modifier = Modifier,
    pitchIndex: Int,
    selected: Boolean,
    onTap: () -> Unit) {
    val keySignature by LOCATOR.get<MidiSettingsRepository>().keySignature.collectAsState()
    val octave by rememberParameterValue(paramKey = StepSequencer.KEY_OCTAVE)

    val midiKey = keySignature.scale.getMidiKey(
        keySignature.baseKey,
        octave.asInt, pitchIndex
    )
    if (midiKey == null) {
        return
    }
    val label = midiKeyName(midiKey)
    Panel(
        containerColor = if (selected) Theme.colors.panelColorAltSelected else Theme.colors.panelColorAlt,
        modifier = modifier.clickable {
            onTap()
        }) {
        Text(modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            text = label)
    }
}
