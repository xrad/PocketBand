package de.nullgrad.pocketband.edit.modulations.usecases

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.modulations.EditModulationsService
import de.nullgrad.pocketband.edit.uimodel.ParameterModulation
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.edit.uimodel.toParameterModulation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetModulationTargetsUseCase(
    private val editModulationsService: EditModulationsService = LOCATOR.get(),
) {
    suspend operator fun invoke(source: ParameterRef) : Flow<List<ParameterModulation>> {
        return flow {
            editModulationsService.modulations.collect { modulations ->
                val mods = modulations.filter { mod ->
                    mod.source.owner.id == source.pluginId &&
                            mod.source.key == source.key
                }
                .mapIndexed { index, it -> it.toParameterModulation(index) }
                emit(mods)
            }
        }
    }
}
