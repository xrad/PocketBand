package de.nullgrad.pocketband.ui.plugins

import de.nullgrad.pocketband.midi.model.NoteType
import de.nullgrad.pocketband.midi.model.midiKeyName
import de.nullgrad.pocketband.plugins.toolbox.WaveTableType
import de.nullgrad.pocketband.ui.utils.amplitude2dB
import java.util.Locale

fun stringListFormatter(v: Double, strings: List<String>): String {
    val maxIndex = strings.lastIndex // Use lastIndex for clarity
    val index = v.toInt().coerceIn(0, maxIndex) // Use coerceIn for range clamping
    return strings[index]
}

fun switchFormatter(v: Double): String {
    return if (v >= 0.5) "on" else "off"
}

fun percentFormatter(v: Double): String {
    return "${(v * 100).toInt()}%" // Use toInt for integer percentage
}

fun frequencyFormatter(v: Double): String {
    if (v > 10000) {
        return String.format(Locale.ROOT, "%.1f kHz", v / 1000)
    }
    if (v > 1000) {
        return String.format(Locale.ROOT, "%.2f Hz", v / 1000)
    }
    return String.format(Locale.ROOT, "%d Hz", v.toInt())
}

fun intFormatter(v: Double): String {
    return v.toInt().toString() // More concise conversion to string
}

val waveformLabels = WaveTableType.entries.map { it.label }.toList() // Use map { it.label } for simpler transformation

fun waveTableTypeFormatter(v: Double): String {
    return stringListFormatter(v, waveformLabels)
}

fun timingFormatter(v: Double): String {
    return "${v.toInt()} ms"
}

fun semiTonesFormatter(v: Double): String {
    return String.format(Locale.ROOT, "%.1f semi", v)
}

fun midiKeyFormatter(v: Double): String {
    val key = v.toInt()
    return midiKeyName(key)
}

fun volumeFormatter(v: Double): String {
    require(v >= 0.0) { "Volume cannot be negative." } // Added check for non-negative volume
    val db = amplitude2dB(v)
    return if (v < 0.001) {
        "-∞ dB"
    } else {
        "%.1f dB".format(db) // Use String.format for formatted string with one decimal place
    }
}

fun noteTypeFormatter(v: Double): String {
    return NoteType.fromDouble(v).label
}
