package de.nullgrad.pocketband.midi.usecases

import androidx.compose.runtime.Immutable
import de.nullgrad.pocketband.midi.model.KeySignature
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.MidiOctave
import de.nullgrad.pocketband.midi.model.maxMidiKey
import de.nullgrad.pocketband.midi.model.midiKeyIsWhite
import de.nullgrad.pocketband.midi.model.midiKeyName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Immutable
data class KeyboardKey(
    val index: Int,
    val midiKey: MidiKey?,
    val label: String,
    val isTonic: Boolean,
    val isWhite: Boolean,
)

@Immutable
data class MidiKeyboardState(
    val scaleMode: Boolean = false,
    val keys: List<KeyboardKey> = emptyList()
)

class FlowKeyboardScaleStepsUseCase {
    operator fun invoke(
        stateIn: CoroutineScope,
        showScale: StateFlow<Boolean>,
        keySignature: StateFlow<KeySignature>,
        octave: StateFlow<MidiOctave>
    ) : StateFlow<MidiKeyboardState> {
        return combine(
            keySignature,
            octave,
            showScale,
            transform = { key : KeySignature, oct : MidiOctave, scale: Boolean ->
                buildScaleSteps(key, oct, scale)
            })
            .stateIn(stateIn, SharingStarted.Eagerly, MidiKeyboardState())
    }

    private fun buildScaleSteps(
        keySignature: KeySignature,
        midiOctave: MidiOctave,
        showScale: Boolean,
    ): MidiKeyboardState {
        return if (showScale) {
            MidiKeyboardState(
                scaleMode = true,
                keys = buildScale(keySignature, midiOctave)
            )
        } else {
            MidiKeyboardState(
                scaleMode = false,
                keys = buildNoScale(keySignature, midiOctave)
            )
        }
    }

    private fun buildScale(
        keySignature: KeySignature,
        midiOctave: MidiOctave,
    ) = buildList {
        repeat(9) { index ->
            val midiKey = keySignature.scale.getMidiKey(keySignature.baseKey, midiOctave, index)
            add(
                KeyboardKey(
                    index = index,
                    midiKey = midiKey,
                    label = midiKey?.let { midiKeyName(it) } ?: "",
                    isTonic = index % keySignature.scale.size == 0,
                    isWhite = if (midiKey == null) false else midiKeyIsWhite(midiKey),
                )
            )
        }
    }


    private fun buildNoScale(
        keySignature: KeySignature,
        midiOctave: MidiOctave,
    ) = buildList {
        val basekey = keySignature.scale.getMidiKey(keySignature.baseKey, midiOctave, 0)!!
        repeat(13) {
            val midiKey = if (basekey + it > maxMidiKey) null else basekey + it
            add(
                KeyboardKey(
                    index = it,
                    midiKey = midiKey,
                    label = midiKey?.let { key -> midiKeyName(key) } ?: "",
                    isTonic = it % 12 == 0,
                    isWhite = if (midiKey == null) false else midiKeyIsWhite(midiKey),
                )
            )
        }
    }
}