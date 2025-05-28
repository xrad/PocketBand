package de.nullgrad.pocketband.edit.modulations.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.modulations.EditModulationsService
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SetModulationUseCase(
    private val editModulationsService: EditModulationsService = LOCATOR.get(),
    private val editPresetService: EditPresetService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke(
        sourceRef: ParameterRef,
        targetRef: ParameterRef,
        amount: Double) = withContext(dispatcher) {
        val modulations = editModulationsService.modulations.value
            .map {
                if (it.source.key == sourceRef.key
                    && it.source.owner.id == sourceRef.owner.id
                    && it.target.key == targetRef.key
                    && it.target.owner.id == targetRef.owner.id) {
                    it.copy(amount = amount)
                }
                else {
                    it
                }
            }
        editModulationsService.updateModulationList(modulations)
        editPresetService.setModified()
    }
}