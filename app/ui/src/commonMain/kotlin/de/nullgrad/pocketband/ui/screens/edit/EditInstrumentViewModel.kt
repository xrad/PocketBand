package de.nullgrad.pocketband.ui.screens.edit

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.edit.plugins.EditService
import de.nullgrad.pocketband.edit.plugins.usecases.CreateProcessorUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.DeleteProcessorUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.ReplaceProcessorUseCase
import de.nullgrad.pocketband.edit.plugins.usecases.SetMuteModuleUseCase
import de.nullgrad.pocketband.edit.uimodel.ModuleRef
import de.nullgrad.pocketband.edit.uimodel.ParameterValue
import de.nullgrad.pocketband.edit.uimodel.toModuleRef
import de.nullgrad.pocketband.edit.uimodel.toParameterValue
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInKind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class ModuleEntry(
    val module: ModuleRef,
    val mute: ParameterValue,
)

@Immutable
data class EditInstrumentState(
    val noteEffects: List<ModuleEntry> = emptyList(),
    val instruments: List<ModuleEntry> = emptyList(),
    val audioEffects: List<ModuleEntry> = emptyList(),
    val modulators: List<ModuleEntry> = emptyList(),
)

private fun PlugIn.toModuleEntry() =
    ModuleEntry(
        module = this.toModuleRef(),
        mute = this.mute.toParameterValue()
    )

class EditInstrumentViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditInstrumentState())
    val state = _state.asStateFlow()

    private val setMuteModuleUseCase = SetMuteModuleUseCase()
    private val createProcessorUseCase = CreateProcessorUseCase()
    private val deleteProcessorUseCase = DeleteProcessorUseCase()
    private val reorderProcessorsUseCase =
        de.nullgrad.pocketband.edit.plugins.usecases.ReorderProcessorUseCase()
    private val replaceProcessorUseCase = ReplaceProcessorUseCase()

    private val editService = LOCATOR.get<EditService>()

    init {
        viewModelScope.launch {
            editService.plugins.collect { plugins ->
                buildState(plugins)
            }
        }
        viewModelScope.launch {
            editService.parameterUpdates.collect { update ->
                if (update.parameter.key == PlugIn.KEY_MUTE) {
                    updateState(update)
                }
            }
        }
    }

    private fun buildState(plugins: List<PlugIn>) {
        _state.value = _state.value.copy(
            noteEffects = plugins
                .filter { it.kind == PlugInKind.NoteEffect }
                .map { it.toModuleEntry() },
            instruments = plugins
                .filter { it.kind == PlugInKind.Instrument }
                .map { it.toModuleEntry() },
            audioEffects = plugins
                .filter { it.kind == PlugInKind.AudioEffect }
                .map { it.toModuleEntry() },
            modulators = plugins
                .filter { it.kind == PlugInKind.Modulator }
                .map { it.toModuleEntry() }
        )
    }

    private fun updateState(update: ParameterValue) {
        fun updateMuteInList(list: List<ModuleEntry>, update: ParameterValue): List<ModuleEntry> {
            return list.map {
                if (it.module == update.parameter.owner)
                    it.copy(mute = update)
                else
                    it
                }
        }
        _state.value = _state.value.copy(
            noteEffects = updateMuteInList(_state.value.noteEffects, update),
            instruments = updateMuteInList(_state.value.instruments, update),
            audioEffects = updateMuteInList(_state.value.audioEffects, update),
            modulators = updateMuteInList(_state.value.modulators, update),
        )
    }

    fun setMute(module: ModuleRef, mute: Boolean) {
        viewModelScope.launch {
            setMuteModuleUseCase(module.id, mute)
        }
    }

    fun createProcessor(type: String) {
        viewModelScope.launch {
            createProcessorUseCase(type)
        }
    }

    fun deleteProcessor(pluginId: Long) {
        viewModelScope.launch {
            deleteProcessorUseCase(pluginId)
        }
    }

    fun reorderProcessors(moveId: Long, beforeId: Long) {
        viewModelScope.launch {
            reorderProcessorsUseCase(moveId, beforeId)
        }
    }

    fun replaceProcessor(type: String, pluginId: Long) {
        viewModelScope.launch {
            replaceProcessorUseCase(type, pluginId)
        }
    }
}
