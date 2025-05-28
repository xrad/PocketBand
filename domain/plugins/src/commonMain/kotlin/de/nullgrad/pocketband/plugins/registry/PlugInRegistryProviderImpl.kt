package de.nullgrad.pocketband.plugins.registry

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.plugins.PlugInRegistryProvider
import de.nullgrad.pocketband.plugins.instruments.FMVoice
import de.nullgrad.pocketband.plugins.instruments.Sampler
import de.nullgrad.pocketband.plugins.instruments.WaveOscillator
import de.nullgrad.pocketband.plugins.modulators.ModGravity
import de.nullgrad.pocketband.plugins.modulators.ModLfo
import de.nullgrad.pocketband.plugins.noteeffects.NoteEcho
import de.nullgrad.pocketband.plugins.noteeffects.Shaker
import de.nullgrad.pocketband.plugins.noteeffects.StepSequencer
import de.nullgrad.pocketband.plugins.processors.Chorus
import de.nullgrad.pocketband.plugins.processors.FreeVerb
import de.nullgrad.pocketband.plugins.processors.LoPass
import de.nullgrad.pocketband.plugins.processors.PRCReverb
import de.nullgrad.pocketband.plugins.processors.Tremolo

internal class PlugInRegistryProviderImpl : PlugInRegistryProvider {
    companion object {
        fun registerService() {
            LOCATOR.register(PlugInRegistryProvider::class) {
                PlugInRegistryProviderImpl()
            }
        }
    }

    override val registry = listOf(
        WaveOscillator.descriptor,
        FMVoice.descriptor,
        Sampler.descriptor,
        ModLfo.descriptor,
        ModGravity.descriptor,
        NoteEcho.descriptor,
        FreeVerb.descriptor,
        PRCReverb.descriptor,
        Chorus.descriptor,
        Tremolo.descriptor,
        StepSequencer.descriptor,
        LoPass.descriptor,
        Shaker.descriptor
    )
}