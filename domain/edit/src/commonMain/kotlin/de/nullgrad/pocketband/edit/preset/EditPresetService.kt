package de.nullgrad.pocketband.edit.preset

import de.nullgrad.pocketband.di.Service
import kotlinx.coroutines.flow.StateFlow

interface EditPresetService : Service {
    val isModified : StateFlow<Boolean>
    fun setNotModified()
    fun setModified()

    val id : StateFlow<Int>
    fun setPresetId(id: Int)

    val name : StateFlow<String>
    fun setPresetName(name: String)
}