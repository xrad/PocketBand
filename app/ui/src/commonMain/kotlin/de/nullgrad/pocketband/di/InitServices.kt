package de.nullgrad.pocketband.di

import de.nullgrad.pocketband.audio.AudioModule
import de.nullgrad.pocketband.audioassets.AudioAssetsModule
import de.nullgrad.pocketband.edit.EditModule
import de.nullgrad.pocketband.engine.EngineModule
import de.nullgrad.pocketband.liveevents.LiveEventsModule
import de.nullgrad.pocketband.midi.MidiModule
import de.nullgrad.pocketband.plugins.PluginModule
import de.nullgrad.pocketband.presets.PresetRepositoryModule
import de.nullgrad.pocketband.recorder.RecorderModule
import de.nullgrad.pocketband.sensors.SensorsModule
import de.nullgrad.pocketband.stk.StkModule
import de.nullgrad.pocketband.synth.SynthModule

fun initServices() {
    // initialize modules
    DiCoroutines.initialize()
    PresetRepositoryModule.initialize()
    AudioModule.initialize()
    SensorsModule.initialize()
    StkModule.initialize()
    AudioAssetsModule.initialize()
    MidiModule.initialize()
    PluginModule.initialize()
    LiveEventsModule.initialize()
    EditModule.initialize()
    SynthModule.initialize()
    RecorderModule.initialize()
    EngineModule.initialize()
}