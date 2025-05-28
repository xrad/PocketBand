package de.nullgrad.pocketband.edit.modulations.service

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.synth.SynthService
import de.nullgrad.pocketband.edit.modulations.EditModulationsService
import de.nullgrad.pocketband.plugins.model.PlugInModulation
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class EditModulationsServiceImpl : EditModulationsService {
    companion object {
        fun registerService() {
            LOCATOR.register(EditModulationsService::class) {
                EditModulationsServiceImpl()
            }
        }
    }

    private val _modulations = MutableStateFlow(emptyList<PlugInModulation>())
    override val modulations = _modulations.asStateFlow()

    private val synthService = LOCATOR.get<SynthService>()

    override fun updateModulationList(list: List<PlugInModulation>) {
        synthService.setModulations(list)
        _modulations.value = list
    }

    override fun createModulation(
        source: PlugInParameter,
        target: PlugInParameter,
        amount: Double
    ): PlugInModulation = PlugInModulation(source = source, target = target, amount = amount)

}