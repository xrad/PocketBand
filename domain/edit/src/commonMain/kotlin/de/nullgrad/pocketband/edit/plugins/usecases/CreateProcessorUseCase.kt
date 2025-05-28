package de.nullgrad.pocketband.edit.plugins.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CreateProcessorUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    private val getNewModuleIdUseCase = GetNewModuleIdUseCase()

    suspend operator fun invoke(type: String) = withContext(dispatcher) {
        val moduleId = getNewModuleIdUseCase()
        // TODO can we replace emptyList with more reasonable initializers?
        val plugin = editService.createPlugin(moduleId, type, emptyList())
        val newList = mutableListOf<PlugIn>()
        for (kind in arrayOf(
            PlugInKind.NoteEffect,
            PlugInKind.Instrument,
            PlugInKind.AudioEffect,
            PlugInKind.Modulator
        )) {
            val subList = editService.plugins.value
                .filter { it.kind == kind }.toMutableList()
            if (plugin.kind == kind) {
                subList.add(plugin)
            }
            newList.addAll(subList)
        }
        editService.updatePluginList(newList)
        editPresetService.setModified()
    }
}