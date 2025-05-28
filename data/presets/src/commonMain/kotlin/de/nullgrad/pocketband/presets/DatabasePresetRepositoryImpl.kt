package de.nullgrad.pocketband.presets

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import de.nullgrad.pocketband.database.PocketBandDatabase
import de.nullgrad.pocketband.presets.model.Preset
import de.nullgrad.pocketband.presets.model.PresetId
import de.nullgrad.pocketband.presets.model.PresetModulation
import de.nullgrad.pocketband.presets.model.PresetModule
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.presets.platform.getPlatform
import de.nullgrad.pocketband.di.IoDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class DatabasePresetRepositoryImpl private constructor(): PresetRepository {
    private val ioDispatcher : CoroutineContext = LOCATOR.get(IoDispatcher::class)

    companion object {
        fun registerService() {
            LOCATOR.register(PresetRepository::class) {
                DatabasePresetRepositoryImpl()
            }
        }
    }

    private val database by lazy {
        val driver = getPlatform().createDriver()
        PocketBandDatabase(driver)
    }

    init {
        initModuleId()
        populateDatabaseIfNeeded()
    }

    private fun populateDatabaseIfNeeded() = runBlocking(ioDispatcher) {
        val q = database.pocketBandDatabaseQueries
        val numPatches = q.countPresets().executeAsOne()
        if (numPatches != NUM_PRESETS.toLong()) {
            for (it in 0 until NUM_PRESETS) {
                val preset = createInitialPatch(it.toLong())
                savePatch(preset)
            }
        }
    }

    override val presetIds: Flow<List<PresetId>> =
        database.pocketBandDatabaseQueries.selectAllPatches(
            mapper = { id, name -> PresetId(id.toInt(), name) }
        )
        .asFlow()
        .mapToList(ioDispatcher)

    override suspend fun createInitialPatch(patchId: Long) : Preset {
        return Preset(
            PresetId(id = patchId.toInt(), name = "Init"),
            modules = listOf(
                PresetModule(
                    id = getNewModuleId(),
                    type = "WaveOscillator",
                    parameters = emptyList())
            ),
            modulations = emptyList()
        )
    }

    private fun getHighestModuleId(): Long {
        return database.pocketBandDatabaseQueries.selectMaxModuleId().executeAsOne()
            .MAX ?: 0L
    }

    private var nextModuleId = 0L
    private fun initModuleId() {
        nextModuleId = getHighestModuleId() + 1
    }

    override suspend fun getNewModuleId() : Long {
        return nextModuleId++
    }

    override suspend fun loadPreset(id: Int): Preset = withContext(ioDispatcher) {
        val dbId = id.toLong()
        val q = database.pocketBandDatabaseQueries
        q.transactionWithResult {
            val patch = q.selectPatch(id = dbId).executeAsOne()
            val modules = q.selectModule(patch_id = dbId).executeAsList()
            val presetModules = modules.map { mod ->
                val parameters = q.selectParameter(module_id = mod.id).executeAsList()
                    .map { param -> PresetParameter(key = param.key, value = param.value_) }
                PresetModule(mod.id, mod.type, parameters)
            }
            val modulations = q.selectModulation(patch_id = dbId).executeAsList()
                .map { PresetModulation(sourceModuleId = it.source_module_id,
                    sourceParameterKey = it.source_parameter_key,
                    targetModuleId = it.target_module_id,
                    targetParameterKey = it.target_parameter_key,
                    amount = it.amount)
                }
            return@transactionWithResult Preset(
                presetId = PresetId(patch.id.toInt(), patch.name),
                modules = presetModules,
                modulations = modulations
            )
        }
    }

    override suspend fun savePatch(preset: Preset) = withContext(ioDispatcher) {
        val q = database.pocketBandDatabaseQueries
        q.transaction {
            // note this will delete all related modules and parameters due to cascade
            q.deletePatch(id = preset.presetId.id.toLong())
            q.insertPatch(id = preset.presetId.id.toLong(), name = preset.presetId.name)
            preset.modules.forEachIndexed { pos, presetModule ->
                q.insertModule(
                    id = presetModule.id,
                    patch_id = preset.presetId.id.toLong(),
                    position = pos.toLong(),
                    type = presetModule.type)
                presetModule.parameters.forEachIndexed { i, parameter ->
                    q.insertParameter(
                        id = i.toLong(),
                        module_id = presetModule.id,
                        key = parameter.key,
                        value_ = parameter.value)
                }
            }
            preset.modulations.forEachIndexed { i, modulation ->
                q.insertModulation(
                    id = i.toLong(),
                    patch_id = preset.presetId.id.toLong(),
                    source_module_id = modulation.sourceModuleId,
                    source_parameter_key = modulation.sourceParameterKey,
                    target_module_id = modulation.targetModuleId,
                    target_parameter_key = modulation.targetParameterKey,
                    amount = modulation.amount)
            }
        }
    }
}
