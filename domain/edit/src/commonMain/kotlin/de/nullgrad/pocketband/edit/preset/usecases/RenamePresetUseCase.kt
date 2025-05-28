package de.nullgrad.pocketband.edit.preset.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.preset.EditPresetService
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class RenamePresetUseCase(
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke(name: String) = withContext(dispatcher) {
        editPresetService.setPresetName(name)
        editPresetService.setModified()
    }
}