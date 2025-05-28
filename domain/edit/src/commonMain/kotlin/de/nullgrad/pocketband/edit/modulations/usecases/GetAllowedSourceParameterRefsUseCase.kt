package de.nullgrad.pocketband.edit.modulations.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.edit.uimodel.toParameterRef
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetAllowedSourceParameterRefsUseCase(
    private val editService: EditService = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
) {
    suspend operator fun invoke() : List<ParameterRef>
        = withContext(dispatcher) {
        editService.plugins.value.flatMap { it.parameters }
            .filter { it.isOutput }
            .map { it.toParameterRef() }
    }
}