package de.nullgrad.pocketband.plugins.toolbox

import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.plugins.model.PlayHead
import de.nullgrad.pocketband.plugins.model.PlugIn
import de.nullgrad.pocketband.plugins.model.PlugInParameter
import de.nullgrad.pocketband.plugins.model.TimingPlugInParameter
import de.nullgrad.pocketband.plugins.model.addPercentParameter
import de.nullgrad.pocketband.plugins.model.addTimingParameter
import de.nullgrad.pocketband.plugins.polysynth.VoiceState
import de.nullgrad.pocketband.plugins.polysynth.VoiceState.Idle
import de.nullgrad.pocketband.plugins.polysynth.VoiceState.Playing
import de.nullgrad.pocketband.plugins.polysynth.VoiceState.Release

class Adsr(plugin: PlugIn, initializer: List<PresetParameter>) {

    companion object {
        const val KEY_ATTACK = "attack"
        const val KEY_DECAY = "decay"
        const val KEY_SUSTAIN = "sustain"
        const val KEY_RELEASE = "release"
    }

    internal val attack: TimingPlugInParameter =
        plugin.addTimingParameter(KEY_ATTACK, 0.0, 5000.0, initializer, 0.1)

    internal val decay: TimingPlugInParameter =
        plugin.addTimingParameter(KEY_DECAY, 0.0, 5000.0, initializer, 0.01)

    internal val sustain: PlugInParameter =
        plugin.addPercentParameter(KEY_SUSTAIN, initializer, 0.8)

    internal val release: TimingPlugInParameter =
        plugin.addTimingParameter(KEY_RELEASE, 0.0, 5000.0, initializer, 0.01)
}

class AdsrVoice(private val owner: Adsr) {

    private var _position = 0.0
    private var _level = 0.0
    private var _attackSlope = 0.0
    private var _decaySlope = 0.0
    private var _releaseSlope = 0.0
    private var _attackUntil = 0.0
    private var _decayUntil = 0.0
    private var _sustainLevel = 1.0

    companion object {
        const val LEVEL_SILENT = 0.0001
    }

    val level: Double
        get() = _level

    fun start(playHead: PlayHead) {
        if (owner.attack.value < 1.0) {
            // No attack
            _level = 1.0
            _attackUntil = 0.0
            _attackSlope = 1000.0
        } else {
            _level = 0.0
            _attackUntil = owner.attack.value / 1000.0
            _attackSlope = 1.0 / (playHead.sampleRate * owner.attack.value / 1000.0)
        }

        if (owner.decay.value < 1.0) {
            // No decay
            _decayUntil = _attackUntil
            _decaySlope = 0.0
        } else {
            _decayUntil = _attackUntil + owner.decay.value / 1000.0
            _decaySlope = - (1 - _sustainLevel) / (playHead.sampleRate * owner.decay.value / 1000.0)
        }
        _sustainLevel = owner.sustain.value
        _releaseSlope = 0.0
        _position = 0.0
    }

    fun release(playHead: PlayHead) {
        _releaseSlope = if (owner.release.value < 1.0) {
            // No release
            -1000.0
        } else {
            -_level / (playHead.sampleRate * owner.release.value / 1000.0)
        }
    }

    fun increment(playHead: PlayHead, state: VoiceState): VoiceState {
        var newState = state
        if (state != Idle) {
            when (state) {
                Playing -> {
                    if (_position < _attackUntil) {
                        _level = (_level + _attackSlope).coerceIn(-1.0, 1.0)
                    } else if (_position < _decayUntil) {
                        _level += _decaySlope
                    } else {
                        _level = _sustainLevel
                    }
                }
                Release -> {
                    _level += _releaseSlope
                    if (_level < LEVEL_SILENT) {
                        newState = Idle
                        _level = 0.0
                    }
                }
                Idle -> { }
            }
            _position += playHead.secondsPerSample
        }
        return newState
    }
}
