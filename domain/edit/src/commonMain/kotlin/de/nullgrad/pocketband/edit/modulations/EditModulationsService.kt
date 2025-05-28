package de.nullgrad.pocketband.edit.modulations

import de.nullgrad.pocketband.di.Service
import de.nullgrad.pocketband.plugins.model.PlugInModulation
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import kotlinx.coroutines.flow.StateFlow

interface EditModulationsService : Service {
    val modulations: StateFlow<List<PlugInModulation>>
    fun createModulation(source: PlugInParameter, target: PlugInParameter, amount: Double) : PlugInModulation
    fun updateModulationList(list: List<PlugInModulation>)
}