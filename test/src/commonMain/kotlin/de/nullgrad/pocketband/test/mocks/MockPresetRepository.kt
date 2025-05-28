package de.nullgrad.pocketband.test.mocks

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.plugins.instruments.Sampler
import de.nullgrad.pocketband.plugins.instruments.WaveOscillator
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.presets.PresetRepository
import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.presets.model.PresetId
import de.nullgrad.pocketband.presets.model.PresetModule
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.presets.model.undefinedPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockPresetRepository : PresetRepository {
    companion object {
        fun registerService() {
            LOCATOR.register(PresetRepository::class) {
                MockPresetRepository()
            }
        }
    }

    private val presets = listOf(
        Preset(
            PresetId(1, "Preset_1"), listOf(
                PresetModule(1, WaveOscillator.PLUGIN_TYPE, emptyList())
            ),
            listOf(),
        ),
        Preset(
            PresetId(2, "Preset_2"), listOf(
                PresetModule(1, Sampler.PLUGIN_TYPE, listOf(
                    PresetParameter(PlugIn.KEY_MUTE, "false"),
                ))
            ),
            listOf(),
        )
    )

    override val presetIds: Flow<List<PresetId>>
        get() = flow { presets.map { it.presetId } }

    private var moduleId = 1
    override suspend fun loadPreset(id: Int): Preset {
        return presets.first { it.presetId.id == id }
    }

    override suspend fun savePatch(preset: Preset) {
    }

    override suspend fun getNewModuleId(): Long {
        return moduleId.toLong().also { moduleId++ }
    }

    override suspend fun createInitialPatch(patchId: Long): Preset {
        return undefinedPreset
    }
}