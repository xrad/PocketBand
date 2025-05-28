package de.nullgrad.pocketband.ui.screens.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.GetPluginParameterValuesUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.ResetParameterUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetBoolParameterUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetNormalizedParameterUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetParameterUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetStringParameterUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.ToggleBoolParameterUseCase
import de.nullgrad.pocketband.edit.preset.EditPresetService
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.edit.uimodel.ParameterRef
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class EditPluginState(
    val currentModule: ModuleRef = ModuleRef.UNDEFINED,
    val moduleParameters: List<ParameterValue> = emptyList(),
) {
    fun getParamValue(key: String): ParameterValue {
        return moduleParameters.find { it.parameter.key == key }
            ?: throw Exception("Parameter $key does not exist.")
    }
}

interface EditParametersOps {
    fun setParameter(parameterRef: ParameterRef, value: Double)
    fun setBoolParameter(parameterRef: ParameterRef, value: Boolean)
    fun setStringParameter(parameterRef: ParameterRef, value: String)
    fun setParameterNormalized(parameterRef: ParameterRef, value: Double)
    fun toggleBoolParameter(parameterRef: ParameterRef)
    fun resetParameter(parameterRef: ParameterRef)
}

class EditPluginViewmodel : ViewModel(), EditParametersOps
{
    private val _state = MutableStateFlow(EditPluginState())
    val state = _state.asStateFlow()

    private val getPluginParameterValuesUseCase = GetPluginParameterValuesUseCase()
    private val setParameterUseCase = SetParameterUseCase()
    private val setStringParameterUseCase = SetStringParameterUseCase()
    private val setBoolParameterUseCase = SetBoolParameterUseCase()
    private val setNormalizedParameterUseCase = SetNormalizedParameterUseCase()
    private val resetParameterUseCase = ResetParameterUseCase()
    private val toggleBoolParameterUseCase = ToggleBoolParameterUseCase()

    private val editService = LOCATOR.get<EditService>()
    private val liveEventsService = LOCATOR.get<de.nullgrad.pocketband.liveevents.LiveEventsService>()
    private val editPresetService = LOCATOR.get<EditPresetService>()

    val liveEvents = liveEventsService.eventsFlow

    init {
        viewModelScope.launch {
            editPresetService.id.collect {
                clearModule()
            }
        }
        viewModelScope.launch {
            editService.parameterUpdates.collect { parameter ->
                if (_state.value.currentModule == ModuleRef.UNDEFINED) {
                    return@collect
                }
                if (parameter.parameter.pluginId != _state.value.currentModule.id) {
                    return@collect
                }
                _state.value = _state.value.copy(
                    moduleParameters = _state.value.moduleParameters.map {
                        if (it.parameter == parameter.parameter) {
                            parameter
                        } else {
                            it
                        }
                    }
                )
            }
        }
        viewModelScope.launch {
            editService.plugins.collect { plugins ->
                if (plugins.find { it.id == _state.value.currentModule.id } == null) {
                    clearModule()
                }
            }
        }
        viewModelScope.launch {
            var subscription = ModuleRef.UNDEFINED.id
            _state.collect {
                liveEventsService.unsubscribePluginUpdates(subscription)
                liveEventsService.subscribePluginUpdates(it.currentModule.id)
                subscription = it.currentModule.id
            }
        }
    }

    fun setModule(module: ModuleRef) {
        viewModelScope.launch {
            val paramValues = getPluginParameterValuesUseCase(module.id)
            _state.value = _state.value.copy(
                currentModule = module,
                moduleParameters = paramValues,
            )
        }
    }

    fun clearModule() {
        _state.value = EditPluginState()
    }

    override fun setParameter(parameterRef: ParameterRef, value: Double) {
        viewModelScope.launch {
            setParameterUseCase(parameterRef.pluginId, parameterRef.key, value)
        }
    }

    override fun setStringParameter(parameterRef: ParameterRef, value: String) {
        viewModelScope.launch {
            setStringParameterUseCase(parameterRef.pluginId, parameterRef.key, value)
        }
    }

    override fun setBoolParameter(parameterRef: ParameterRef, value: Boolean) {
        viewModelScope.launch {
            setBoolParameterUseCase(parameterRef.pluginId, parameterRef.key, value)
        }
    }

    override fun setParameterNormalized(parameterRef: ParameterRef, value: Double) {
        viewModelScope.launch {
            setNormalizedParameterUseCase(parameterRef.pluginId, parameterRef.key, value)
        }
    }

    override fun resetParameter(parameterRef: ParameterRef) {
        viewModelScope.launch {
            resetParameterUseCase(parameterRef.pluginId, parameterRef.key)
        }
    }

    override fun toggleBoolParameter(parameterRef: ParameterRef) {
        viewModelScope.launch {
            toggleBoolParameterUseCase(parameterRef.pluginId, parameterRef.key)
        }
    }
}

@Composable
fun rememberCurrentModule(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) : State<ModuleRef> {
    val editPluginState by editPluginViewModel.state.collectAsState()
    return remember {
        derivedStateOf {
            editPluginState.currentModule
        }
    }
}

class ParameterViewModel(
    state: State<ParameterValue>,
    api: EditParametersOps,
) : State<ParameterValue> by state, EditParametersOps by api

@Composable
fun rememberParameterValue(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
    paramKey: String
) : ParameterViewModel {
    val editPluginState by editPluginViewModel.state.collectAsState()
    val state = remember {
        derivedStateOf {
            editPluginState.getParamValue(paramKey)
        }
    }
    return remember {
        ParameterViewModel(state = state, api = editPluginViewModel)
    }
}