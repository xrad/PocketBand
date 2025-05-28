package de.nullgrad.pocketband.edit.modulations.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.edit.uimodel.toModuleRef
import de.nullgrad.pocketband.plugins.model.PlugInKind
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetAllowedSourceModulesUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke(pluginId: Long): List<ModuleRef> = withContext(dispatcher) {
        editService.plugins.value
            .filter { it.id != pluginId && it.kind == PlugInKind.Modulator }
            .map { it.toModuleRef() }
    }
}
