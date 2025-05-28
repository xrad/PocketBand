package de.nullgrad.pocketband.plugins.model

import androidx.annotation.CallSuper
import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.midi.model.MidiData

typealias OnParameterChange = (PlugInParameter) -> Unit // Function type for parameter change listener

abstract class PlugIn(val id: Long) {
    abstract val plugInDescriptor: PluginDescriptor

    private val _parameters = mutableListOf<PlugInParameter>()
    val parameters: List<PlugInParameter> = _parameters

    val label: String
        get() = plugInDescriptor.label

    val type: String
        get() = plugInDescriptor.type

    val kind: PlugInKind
        get() = plugInDescriptor.kind

    // TODO: Replace emptyList with actual initialization logic
    val mute = addBoolParameter(KEY_MUTE, emptyList(), false)

    val isMuted: Boolean
        get() = mute.effectiveValue >= 0.5

    var muted: Boolean
        get() = mute.value >= 0.5
        set(value) {
            mute.value = if (value) 1.0 else 0.0
        }

    private val running = addBoolParameter(KEY_RUNNING, emptyList(), false, isOutput = true)

    // Sub-classes can override
    @CallSuper
    open fun onParameterChange(parameter: PlugInParameter) {
        listeners.forEach { it(parameter) }
    }

    private val listeners = mutableListOf<OnParameterChange>()

    fun addParameterChangeListener(listener: OnParameterChange) {
        listeners.add(listener)
    }

    fun removeParameterChangeListener(listener: OnParameterChange) {
        listeners.remove(listener)
    }

    private fun removeAllParameterChangeListeners() {
        listeners.clear()
    }

    @CallSuper
    open fun dispose() {
        if (running.boolValue) {
            throw Exception("Plugin not stopped")
        }
        removeAllParameterChangeListeners()
    }

    internal fun addParameter(parameter: PlugInParameter) {
        _parameters.add(parameter)
    }

    @CallSuper
    open fun start(playHead: PlayHead) {
        running.value = 1.0
    }

    abstract suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData)

    @CallSuper
    open fun stop(playHead: PlayHead) {
        running.value = 0.0
    }

    companion object {
        const val KEY_MUTE = "mute"
        const val KEY_RUNNING = "running"
    }
}
