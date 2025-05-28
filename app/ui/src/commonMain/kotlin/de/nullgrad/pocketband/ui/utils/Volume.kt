package de.nullgrad.pocketband.ui.utils

import kotlin.math.log10
import kotlin.math.pow

fun amplitude2dB(amplitude: Double): Double {
    require(amplitude > 0.0) { "Amplitude must be positive." }
    return 20 * log10(amplitude)
}

fun dB2Amplitude(db: Double): Double {
    return 10.0.pow(db / 20.0)
}
