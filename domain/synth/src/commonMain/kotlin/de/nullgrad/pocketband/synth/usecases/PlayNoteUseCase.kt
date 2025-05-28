package de.nullgrad.pocketband.synth.usecases

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.synth.SynthService
import de.nullgrad.pocketband.midi.MidiSettingsRepository
import de.nullgrad.pocketband.midi.model.ChordType
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

class PlayNoteUseCase(
    private val synthService: SynthService = LOCATOR.get(),
    private val midiSettingsRepository: MidiSettingsRepository = LOCATOR.get(),
    private val dispatcher: CoroutineContext = LOCATOR.get(DefaultDispatcher::class)
)
{
    suspend operator fun invoke(key: Int, velocity: Int, playChords: Boolean, duration: Duration)
        = withContext(dispatcher) {
        val keys = if (playChords) {
            val keySignature = midiSettingsRepository.keySignature.value
            ChordType.Triad.getKeys(keySignature, key)
        } else {
            listOf(key)
        }
        for (k in keys) {
            synthService.keyDown(k, velocity)
        }
        delay(duration)
        for (k in keys) {
            synthService.keyUp(k, velocity)
        }
    }
}