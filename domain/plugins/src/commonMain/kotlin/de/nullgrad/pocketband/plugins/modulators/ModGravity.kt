package de.nullgrad.pocketband.plugins.modulators

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.liveevents.PluginUpdateProvider
import de.nullgrad.pocketband.liveevents.model.PluginUpdate
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PluginDescriptor
import de.nullgrad.pocketband.plugins.model.addDoubleParameter
import de.nullgrad.pocketband.plugins.nullPlugin
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.presets.model.undefinedPreset
import de.nullgrad.pocketband.sensors.model.SensorEvent
import de.nullgrad.pocketband.sensors.SensorService
import de.nullgrad.pocketband.sensors.model.SensorType

@Immutable
data class GravityLiveState(
    override val pluginId: Long = nullPlugin.id,
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
) : PluginUpdate

class ModGravity(id: Long, initializer: List<PresetParameter>) : PlugIn(id), PluginUpdateProvider {
    companion object {
        const val PLUGIN_TYPE = "ModGravity"
        val descriptor = PluginDescriptor(
            type = PLUGIN_TYPE,
            label = "Gravity",
            kind = PlugInKind.Modulator,
            createPlugin = { id, initializer -> ModGravity(id, initializer) },
        )
        const val KEY_OUTPUTX = "outputx"
        const val KEY_OUTPUTY = "outputy"
        const val KEY_OUTPUTZ = "outputz"
    }

    override val plugInDescriptor: PluginDescriptor = descriptor

    private val outputx =
        addDoubleParameter(KEY_OUTPUTX, -1.0, 1.0, initializer, 0.0, isOutput = true)
    private val outputy =
        addDoubleParameter(KEY_OUTPUTY, -1.0, 1.0, initializer, 0.0, isOutput = true)
    private val outputz =
        addDoubleParameter(KEY_OUTPUTZ, -1.0, 1.0, initializer, 0.0, isOutput = true)

    private val sensorManager = LOCATOR.get<SensorService>().sensorManager

    private var currentX: Double = 0.0
    private var currentY: Double = 0.0
    private var currentZ: Double = 0.0

    private fun eventListener(event: SensorEvent) {
        currentX = event.dataX / 9.81
        currentY = event.dataY / 9.81
        currentZ = event.dataZ / 9.81
    }

    override fun start(playHead: PlayHead) {
        super.start(playHead)
        sensorManager.sensors.find { it.sensorType == SensorType.Gravity }?.listen(::eventListener)
    }

    override fun stop(playHead: PlayHead) {
        super.stop(playHead)
        sensorManager.sensors.find { it.sensorType == SensorType.Gravity }?.unlisten(::eventListener)
    }

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        outputx.value = currentX
        outputy.value = currentY
        outputz.value = currentZ
    }

    override fun getPluginUpdate(): PluginUpdate {
        return GravityLiveState(id, outputx.value, outputy.value, outputz.value)
    }
}