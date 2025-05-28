package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Composable
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.plugins.model.PlugIn

interface PluginUi {
    val type: String
    val parameterLabel: Map<String, String>
    @Composable
    fun createController()
}

val pluginUiRegistry = listOf(
    WaveOscillatorUi,
    FMVoiceUi,
    SamplerUi,
    FreeVerbUi,
    PRCReverbUi,
    ChorusUi,
    TremoloUi,
    ModLfoUi,
    ModGravityUi,
    NoteEchoUi,
    StepSequencerUi,
    LoPassUi,
    ShakerUi,
)

fun getParameterLabel(moduleType: String, paramKey: String) : String {
    return when (paramKey) {
        PlugIn.KEY_MUTE -> "Mute"
        PlugIn.KEY_RUNNING -> "Running"
        else -> pluginUiRegistry.firstOrNull { it.type == moduleType }
            ?.parameterLabel?.get(paramKey) ?: paramKey
    }
}

@Composable
fun ModuleRef.CreatePluginUi() {
    pluginUiRegistry.firstOrNull { it.type == type }
        ?.createController()
}