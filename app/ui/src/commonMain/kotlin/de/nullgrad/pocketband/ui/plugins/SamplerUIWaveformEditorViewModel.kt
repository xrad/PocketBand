package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.audioassets.AudioAssetsRepository
import de.nullgrad.pocketband.audioassets.model.AudioFile
import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.GetPluginParameterValuesUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetParameterUseCase
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.plugins.instruments.Sampler
import de.nullgrad.pocketband.ui.utils.scaleAndMergeChannels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Immutable
data class WaveformMarker(
    val position: Int,
    val moving: Boolean,
)

@Immutable
data class WaveformData(
    val waveform: DoubleArray = doubleArrayOf(),
    val numRawSamples: Int = 0,
    val numScaledSamples: Int = 0,
    val zoomLevel: Double = 1.0,
    val displayOffset: Int = 0,
    val displayWidth: Int = 0,
    val startMarker: WaveformMarker = WaveformMarker(0, false),
    val stopMarker: WaveformMarker = WaveformMarker(0, false),
) {
    val moving: Boolean get() = startMarker.moving ||stopMarker.moving

    private val waveformContentHash = waveform.contentHashCode()

    fun indexScaled(rawSamplePos: Int) : Int {
        return if (numRawSamples != 0)
            rawSamplePos * numScaledSamples / numRawSamples else 0
    }

    fun indexRaw(scaledSamplePos: Int) : Int {
        return if (numScaledSamples != 0)
            scaledSamplePos * numRawSamples / numScaledSamples else 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WaveformData) return false
        if (!super.equals(other)) return false
        return waveform.contentEquals(other.waveform)
    }

    override fun hashCode(): Int {
        return 31 * super.hashCode() + waveformContentHash
    }
}

class WaveformEditorViewModel(
    private val currentModule: ModuleRef,
    private val editService: EditService = LOCATOR.get(),
) : ViewModel() {
    val audioAssets: AudioAssetsRepository = LOCATOR.get()

    private val _waveformState = MutableStateFlow(WaveformData())
    val waveformState = _waveformState.asStateFlow()

    private val setParameterUseCase = SetParameterUseCase()
    private val getPluginParameterValuesUseCase = GetPluginParameterValuesUseCase()

    private var audioFile: AudioFile? = null
    private var waveform: DoubleArray? = null

    private var displayWidth = 0

    init {
        viewModelScope.launch {
            initSampleParameters()
            watchSampleParameters()
        }
    }

    private suspend fun initSampleParameters() {
        getPluginParameterValuesUseCase(currentModule.id).forEach { parameter ->
            updateParameter(parameter)
        }
        updateWave(samplesChanged = true)
    }

    private suspend fun watchSampleParameters() {
        editService.parameterUpdates.collect {
            updateParameter(it)
        }
    }

    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
    private suspend fun updateWave(
        newDisplayWidth: Int = displayWidth,
        zoomLevel: Double = _waveformState.value.zoomLevel,
        displayOffset: Int = _waveformState.value.displayOffset,
        samplesChanged: Boolean = false,
    ) = withContext(dispatcher) {
        displayWidth = newDisplayWidth
        val audioFile = audioFile ?: return@withContext

        val newZoomLevel = zoomLevel.coerceAtLeast(1.0)

        val numScaledSamples = (displayWidth * newZoomLevel)
            .roundToInt()
            .coerceIn(displayWidth, audioFile.numSamples)

        var newDisplayOffset = displayOffset
        if (newDisplayOffset + displayWidth > _waveformState.value.numScaledSamples) {
            newDisplayOffset = numScaledSamples - displayWidth
        }
        if (newDisplayOffset < 0) {
            newDisplayOffset = 0
        }

        // update waveform if needed
        if (waveform == null || samplesChanged || _waveformState.value.numScaledSamples != numScaledSamples) {
            waveform = scaleAndMergeChannels(numScaledSamples, audioFile.soundData)
        }

        _waveformState.value = _waveformState.value.copy(
            waveform = waveform!!,
            numRawSamples = audioFile.numSamples,
            numScaledSamples = numScaledSamples,
            displayWidth = displayWidth,
            displayOffset = newDisplayOffset,
            zoomLevel = newZoomLevel,
        )
    }

    private suspend fun updateParameter(parameter: ParameterValue) {
        when(parameter.parameter.key) {
            Sampler.KEY_SAMPLE -> {
                val sample = parameter.valueStr
                if (audioFile == null || audioFile!!.path != sample) {
                    audioAssets.loadAudioFile(sample)?.let {
                        audioFile = it
                        updateWave(samplesChanged = true)
                    }
                }
            }
            Sampler.KEY_START_SAMPLE -> {
                val startPosition = parameter.asInt
                _waveformState.value = _waveformState.value.copy(
                    startMarker = _waveformState.value.startMarker.copy(
                        position = startPosition
                    ))
            }
            Sampler.KEY_STOP_SAMPLE -> {
                val stopPosition = parameter.asInt
                _waveformState.value = _waveformState.value.copy(
                    stopMarker = _waveformState.value.stopMarker.copy(
                        position = stopPosition
                    ))
            }
        }
    }

    fun setSize(size: IntSize) {
        viewModelScope.launch {
            updateWave(newDisplayWidth = size.width)
        }
    }

    fun updateView(zoomChange: Float, offsetChange: Offset) {
        viewModelScope.launch {
            updateWave(
                zoomLevel = _waveformState.value.zoomLevel * zoomChange,
                displayOffset = _waveformState.value.displayOffset - offsetChange.x.roundToInt()
            )
        }
    }

    fun resetView() {
        viewModelScope.launch {
            updateWave(zoomLevel = 1.0, displayOffset = 0)
        }
    }

    private fun isTap(markerPos: Int, p: Offset, displayOffset: Int) : Boolean {
        val scaledMarkerPos = _waveformState.value.indexScaled(markerPos)
        return abs(displayOffset + p.x.toInt() - scaledMarkerPos) < 30
    }

    fun tap(offset: Offset) {
        if (isTap(_waveformState.value.startMarker.position, offset, _waveformState.value.displayOffset)) {
            _waveformState.value = _waveformState.value.copy(
                startMarker = _waveformState.value.startMarker.copy(moving = true)
            )
        }
        else if (isTap(_waveformState.value.stopMarker.position, offset, _waveformState.value.displayOffset)) {
            _waveformState.value = _waveformState.value.copy(
                stopMarker = _waveformState.value.stopMarker.copy(moving = true)
            )
        }
    }

    fun move(offset: Offset) {
        viewModelScope.launch {
            if (_waveformState.value.startMarker.moving) {
                val minAllowed = 0
                val maxAllowed = min(
                    _waveformState.value.stopMarker.position,
                    _waveformState.value.numRawSamples - 1
                )
                val position = _waveformState.value.indexRaw(
                    _waveformState.value.displayOffset + offset.x.toInt()
                ).coerceIn(minAllowed, maxAllowed)
                setParameterUseCase(currentModule.id, Sampler.KEY_START_SAMPLE, position.toDouble())
            }
            if (_waveformState.value.stopMarker.moving) {
                val minAllowed = max(0, _waveformState.value.startMarker.position)
                val maxAllowed = _waveformState.value.numRawSamples - 1
                val position = _waveformState.value.indexRaw(
                    _waveformState.value.displayOffset + offset.x.toInt()
                ).coerceIn(minAllowed, maxAllowed)
                setParameterUseCase(currentModule.id, Sampler.KEY_STOP_SAMPLE, position.toDouble())
            }
        }
    }

    fun finishMove() {
        _waveformState.value = _waveformState.value.copy(
            startMarker = _waveformState.value.startMarker.copy(moving = false),
            stopMarker = _waveformState.value.stopMarker.copy(moving = false)
        )
    }
}