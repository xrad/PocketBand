package de.nullgrad.pocketband.presets

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.presets.model.PresetId
import kotlinx.coroutines.flow.Flow

interface PresetRepository : Service {
    val presetIds: Flow<List<PresetId>>
    suspend fun loadPreset(id: Int): Preset
    suspend fun savePatch(preset: Preset)
    suspend fun getNewModuleId() : Long
    suspend fun createInitialPatch(patchId: Long) : Preset
}

const val NUM_PRESETS = 32
