package de.nullgrad.pocketband.synth.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.midi.MidiSettingsRepository
import de.nullgrad.pocketband.midi.model.ChordType
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiVelocity
import de.nullgrad.pocketband.synth.SynthService
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

enum class KeyOperation {
    KEY_DOWN,
    KEY_UP,
}

class KeyUpDownUseCase(
    private val synthService: SynthService = LOCATOR.get(),
    private val midiSettingsRepository: MidiSettingsRepository = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class),
    val operation: KeyOperation,
)
{
    val keyOp : (key: MidiKey, velocity: MidiVelocity) -> Unit =
        when (operation) {
            KeyOperation.KEY_DOWN -> synthService::keyDown
            KeyOperation.KEY_UP -> synthService::keyUp
        }

    suspend operator fun invoke(key: Int, velocity: Int, playChords: Boolean)
        = withContext(dispatcher) {
        val keys = if (playChords) {
            val keySignature = midiSettingsRepository.keySignature.value
            ChordType.Triad.getKeys(keySignature, key)
        } else {
            listOf(key)
        }
        for (k in keys) {
            keyOp(k, velocity)
        }
    }
}