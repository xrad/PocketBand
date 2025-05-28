package de.nullgrad.pocketband.plugins

import de.nullgrad.pocketband.audio.model.AudioData
import de.nullgrad.pocketband.midi.model.MidiData
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import de.nullgrad.pocketband.plugins.model.PluginDescriptor

object NullPlugin : PlugIn(id = 0) {
    override val plugInDescriptor: PluginDescriptor = PluginDescriptor(
        type = "NullEffect",
        label = "Null Effect",
        kind = PlugInKind.Undefined,
        createPlugin = { _, _ ->  throw Exception("The null plugin cannot be instantiated.") }
    )

    override suspend fun process(playHead: PlayHead, audioData: AudioData, midiData: MidiData) {
        // No processing needed for the null plugin
    }
}

val nullPlugin = NullPlugin
