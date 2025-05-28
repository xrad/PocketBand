package de.nullgrad.pocketband.ui.plugins

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.edit.modulations.usecases.AddModulationUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.GetAllowedTargetModulesUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.GetAllowedTargetParameterRefsUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.GetModulationTargetsUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.RemoveModulationUseCase
import de.nullgrad.pocketband.edit.modulations.usecases.SetModulationUseCase
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.edit.uimodel.ParameterModulation
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.edit.uimodel.usecases.GetParameterRefUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SelectionMode {
    None,
    Module,
    Parameter,
}

@Immutable
data class AddModulationState(
    val selectionMode: SelectionMode = SelectionMode.None,
    val modulationTargets: List<ParameterModulation> = emptyList(),
    val selectedModule: ModuleRef = ModuleRef.UNDEFINED,
    val allowedTargetModules: List<ModuleRef> = emptyList(),
    val allowedTargetParameters: List<ParameterRef> = emptyList(),
)

class ModulationTargetsViewModel(private val module: ModuleRef, private val sourceKey: String)
    : ViewModel()
{
    private val _state = MutableStateFlow(AddModulationState())
    val state = _state.asStateFlow()

    private lateinit var sourceRef: ParameterRef

    private val setModulationUseCase = SetModulationUseCase()
    private val removeModulationUseCase = RemoveModulationUseCase()
    private val addModulationUseCase = AddModulationUseCase()
    private val getParameterRefUseCase = GetParameterRefUseCase()
    private val getModulationTargetsUseCase = GetModulationTargetsUseCase()
    private val getAllowedTargetParameterRefsUseCase = GetAllowedTargetParameterRefsUseCase()
    private val getAllowedTargetModulesUseCase = GetAllowedTargetModulesUseCase()

    init {
        viewModelScope.launch {
            sourceRef = getParameterRefUseCase(module.id, sourceKey)
            getModulationTargetsUseCase(sourceRef).collect { modulations ->
                _state.value = _state.value.copy(
                    modulationTargets = modulations
                )
            }
        }
    }

    fun cancelSelection() {
        _state.value = AddModulationState()
    }

    fun requestModuleSelection() {
        viewModelScope.launch {
            val allowedTargetModules = getAllowedTargetModulesUseCase(sourceRef.pluginId)
            _state.value = _state.value.copy(
                selectionMode = SelectionMode.Module,
                allowedTargetModules = allowedTargetModules
            )
        }
    }

    fun requestParameterSelectionForModule(module: ModuleRef) {
        viewModelScope.launch {
            val allowedTargetParameters = getAllowedTargetParameterRefsUseCase(module.id)
                .filterNot { ref -> _state.value.modulationTargets.any { it.target == ref } }
            _state.value = _state.value.copy(
                selectionMode = SelectionMode.Parameter,
                selectedModule = module,
                allowedTargetModules = emptyList(),
                allowedTargetParameters = allowedTargetParameters,
            )
        }
    }

    fun addModulation(target: ParameterRef, amount: Double) {
        viewModelScope.launch {
            addModulationUseCase(sourceRef, target, amount)
        }
    }

    fun removeModulation(targetRef: ParameterRef) {
        viewModelScope.launch {
            removeModulationUseCase(sourceRef, targetRef)
        }
    }

    fun resetModulation(targetRef: ParameterRef) {
        viewModelScope.launch {
            setModulationUseCase(sourceRef, targetRef, 0.0)
        }
    }

    fun setModulation(targetRef: ParameterRef, amount: Double) {
        viewModelScope.launch {
            setModulationUseCase(sourceRef, targetRef, amount)
        }
    }
}
