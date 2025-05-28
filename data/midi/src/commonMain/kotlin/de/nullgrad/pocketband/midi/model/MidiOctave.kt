package de.nullgrad.pocketband.midi.model

typealias MidiOctave = Int

const val minMidiOctave = -2
const val maxMidiOctave = minMidiOctave + numMidiKeys / 12
