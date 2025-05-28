package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.GetPluginParameterValuesUseCase
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.plugins.toolbox.Adsr
import de.nullgrad.pocketband.ui.screens.edit.rememberCurrentModule
import de.nullgrad.pocketband.ui.utils.rememberTemporaryViewmodel
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import de.nullgrad.pocketband.ui.widgets.ParameterRow
import de.nullgrad.pocketband.ui.widgets.ParameterSlider
import kotlinx.coroutines.launch

val adsrParameterLabels = mapOf(
    Adsr.KEY_ATTACK to "Attack",
    Adsr.KEY_DECAY to "Decay",
    Adsr.KEY_SUSTAIN to "Sustain",
    Adsr.KEY_RELEASE to "Release",
)

@Composable
fun AdsrUi() {
    ParameterRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        AdsrEnvelope(modifier = Modifier.fillMaxHeight().weight(2f))
        ParameterColumn(
            modifier = Modifier.fillMaxHeight().weight(1f),
        ) {
            ParameterSlider(
                paramKey = Adsr.KEY_ATTACK,
                formatValue = ::timingFormatter,
            )
            ParameterSlider(
                paramKey = Adsr.KEY_DECAY,
                formatValue = ::timingFormatter
            )
            ParameterSlider(
                paramKey = Adsr.KEY_SUSTAIN,
                formatValue = ::percentFormatter,
            )
            ParameterSlider(
                paramKey = Adsr.KEY_RELEASE,
                formatValue = ::timingFormatter
            )
        }
    }
}

class WatchParametersViewModel(
    private val currentModule: ModuleRef,
    private val keys: List<String>,
    private val editService: EditService = LOCATOR.get(),
) : ViewModel() {
    private val _mapState = mutableStateOf<Map<String, ParameterValue>>(emptyMap())
    val mapState: Map<String, ParameterValue> by _mapState

    private val getPluginParameterValuesUseCase = GetPluginParameterValuesUseCase()

    init {
        viewModelScope.launch {
            initSampleParameters()
            watchSampleParameters()
        }
    }

    private suspend fun initSampleParameters() {
        val mutableMap = mapState.toMutableMap()
        getPluginParameterValuesUseCase(currentModule.id)
            .filter { it.parameter.key in keys }
            .forEach { mutableMap[it.parameter.key] = it }
        _mapState.value = mutableMap
    }

    private suspend fun watchSampleParameters() {
        editService.parameterUpdates.collect {
            if (it.parameter.key in keys) {
                _mapState.value = mapState.toMutableMap().apply {
                    this[it.parameter.key] = it
                }
            }
        }
    }
}

// sustain will be as long as the note is sounding - assume 1 s here for display
private const val sustainDurationMs = 1000f

@Composable
fun AdsrEnvelope(
    modifier: Modifier = Modifier,
) {
    val currentModule by rememberCurrentModule()
    val viewModel = rememberTemporaryViewmodel() {
        WatchParametersViewModel(
            currentModule = currentModule,
            keys = listOf(Adsr.KEY_ATTACK, Adsr.KEY_DECAY, Adsr.KEY_SUSTAIN, Adsr.KEY_RELEASE),
        )
    }
    VisualizerBox(modifier) {
        Spacer(Modifier
            .fillMaxSize()
            .drawWithCache {
                val envColor = Color.Green
                val envStroke = 10f
                val envStyle = Stroke(width = envStroke)
                val envSustainStyle = Stroke(width = envStroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                val attack = viewModel.mapState[Adsr.KEY_ATTACK]
                val decay = viewModel.mapState[Adsr.KEY_DECAY]
                val sustain = viewModel.mapState[Adsr.KEY_SUSTAIN]
                val release = viewModel.mapState[Adsr.KEY_RELEASE]
                var attackDecayPath : Path? = null
                var sustainPath : Path? = null
                var releasePath : Path? = null
                if (attack != null && decay != null && sustain != null && release != null) {
                    val totalDuration = attack.value + decay.value + sustainDurationMs + release.value
                    val dpPerMs = size.width / totalDuration.toFloat()
                    val xAttack = (dpPerMs * attack.value).toFloat()
                    val xDecay = (dpPerMs * decay.value).toFloat()
                    val xSustain = dpPerMs * 1000f
                    val xRelease = (dpPerMs * release.value).toFloat()
                    val ySustain = (size.height - size.height * sustain.value).toFloat()
                    attackDecayPath = Path().apply {
                        moveTo(0f, size.height)
                        lineTo(xAttack,0f)
                        lineTo(xAttack + xDecay, ySustain)
                    }
                    sustainPath = Path().apply {
                        moveTo(xAttack + xDecay, ySustain)
                        lineTo(xAttack + xDecay + xSustain, ySustain)
                    }
                    releasePath = Path().apply {
                        moveTo(xAttack + xDecay + xSustain, ySustain)
                        lineTo(xAttack + xDecay + xSustain + xRelease, size.height)
                    }
                }
                onDrawBehind {
                    if (attackDecayPath != null) {
                        drawPath(attackDecayPath, envColor, style = envStyle)
                    }
                    if (sustainPath != null) {
                        drawPath(sustainPath, envColor, style = envSustainStyle)
                    }
                    if (releasePath != null) {
                        drawPath(releasePath, envColor, style = envStyle)
                    }
                }
            }
        )
    }
}
