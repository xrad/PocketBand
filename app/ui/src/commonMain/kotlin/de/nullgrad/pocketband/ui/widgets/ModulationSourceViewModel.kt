package de.nullgrad.pocketband.ui.widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.edit.modulations.usecases.AddModulationUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.GetAllowedSourceParameterRefsUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.GetModulationSourcesUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.RemoveModulationUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.SetModulationUseCase
import de.nullgrad.pocketband.edit.uimodel.ParameterModulation
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModulationSourceViewModel(private val targetRef: ParameterRef)
    : ViewModel()
{
    private val getModulationSourcesUseCase = GetModulationSourcesUseCase()
    private val setModulationUseCase = SetModulationUseCase()
    private val removeModulationUseCase = RemoveModulationUseCase()
    private val addModulationUseCase = AddModulationUseCase()
    private val getAllowedSourceParameterRefsUseCase = GetAllowedSourceParameterRefsUseCase()

    val _sources = MutableStateFlow(emptyList<ParameterModulation>().toImmutableList())
    val sources = _sources.asStateFlow()

    val _allowedSourceParameters: MutableStateFlow<ImmutableList<ParameterRef>> = MutableStateFlow(persistentListOf())
    val allowedSourceParameters = _allowedSourceParameters.asStateFlow()

    init {
        viewModelScope.launch {
            getModulationSourcesUseCase(targetRef).collect { modulations ->
                _sources.value = modulations.toImmutableList()
                _allowedSourceParameters.value =
                    getAllowedSourceParameterRefsUseCase()
                        .filterNot { ref -> _sources.value.any { it.source == ref } }
                        .toImmutableList()
            }
        }
    }

    fun addModulation(sourceRef: ParameterRef, amount: Double) {
        viewModelScope.launch {
            addModulationUseCase(sourceRef, targetRef, amount)
        }
    }

    fun removeModulation(sourceRef: ParameterRef) {
        viewModelScope.launch {
            removeModulationUseCase(sourceRef, targetRef)
        }
    }

    fun resetModulation(sourceRef: ParameterRef) {
        viewModelScope.launch {
            setModulationUseCase(sourceRef, targetRef, 0.0)
        }
    }

    fun setModulation(sourceRef: ParameterRef, amount: Double) {
        viewModelScope.launch {
            setModulationUseCase(sourceRef, targetRef, amount)
        }
    }
}