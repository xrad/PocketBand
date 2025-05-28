package de.nullgrad.pocketband.synth.service

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.plugins.model.PlugInModulation
import de.nullgrad.pocketband.plugins.model.PlugInParameter

internal class SynthModulation {
    @Immutable
    private data class ModEntry(val target: PlugInParameter) {
        val mods: MutableList<PlugInModulation> = mutableListOf()
    }

    private val modulationTargets: MutableMap<String, ModEntry> = mutableMapOf()

    // - Reset modulations in case a modulation is removed. Otherwise
    // modulationNormalized will be recalculated in the next cycle anyway.
    // see applyModulations()
    // - Clear isModulated flag, reset below if still present needed
    fun setModulations(list: List<PlugInModulation>) {
        modulationTargets.forEach {
            it.value.target.modulationNormalized = 0.0
            it.value.target.isModulated = false
        }
        modulationTargets.clear()
        for (mod in list) {
            val entry = modulationTargets[mod.target.id] ?: ModEntry(mod.target)
            entry.mods.add(mod)
            modulationTargets[mod.target.id] = entry
        }
        modulationTargets.forEach {
            it.value.target.isModulated = true
        }
    }

    fun apply() {
        for (entry in modulationTargets.values) {
            var modValue = 0.0
            for (mod in entry.mods) {
                val source = mod.source
                if (!source.owner.isMuted) {
                    val modulation = if (source.max <= 1.0) source.value else source.normalizedValue
                    modValue += mod.amount * modulation
                }
            }
            entry.target.modulationNormalized = modValue
        }
    }
}