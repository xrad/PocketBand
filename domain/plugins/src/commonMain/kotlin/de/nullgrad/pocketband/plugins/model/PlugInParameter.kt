package de.nullgrad.pocketband.plugins.model

import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.midi.model.MidiKey
import de.nullgrad.pocketband.midi.model.NoteType
import de.nullgrad.pocketband.midi.model.maxNoteTypeIndex
import de.nullgrad.pocketband.midi.model.minNoteTypeIndex
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sqrt

open class PlugInParameter(
    val owner: PlugIn,
    val key: String,
    open val min: Double,
    open val max: Double,
    val initialValue: Double,
    val defaultValue: Double,
    val isOutput: Boolean = false
) {
    val id: String = owner.id.toString() + key

    open val range: Double by lazy { max - min }

    // indicator telling if this parameter is being modulated by one or many sources
    var isModulated: Boolean = false


    private var _value: Double = initialValue
    open var value: Double
        get() = _value.coerceIn(min, max)
        set(v) {
            if (_value != v) {
                _value = v.coerceIn(min, max)
                _effectiveValueValid = false
                notifyChange()
            }
        }

    open var valueStr: String
        get() = _value.toString()
        set(s) {
            val newValue = s.toDoubleOrNull() ?: return // Handle potential parsing errors
            if (value != newValue) {
                value = newValue
                notifyChange()
            }
        }

    var normalizedValue: Double
        get() = normalize(value)
        set(value) {
            this.value = denormalize(value)
        }

    val normalizedDefaultValue: Double
        get() = normalize(defaultValue)

    private var _modulationNormalized = 0.0
    var modulationNormalized: Double
        get() = _modulationNormalized.coerceIn(-1.0, 1.0)
        set(value) {
            val newValue = value.coerceIn(-1.0, 1.0)
            if (newValue != _modulationNormalized) {
                _modulationNormalized = value.coerceIn(-1.0, 1.0)
                _effectiveValueValid = false
                notifyChange()
            }
        }

    val modulation: Double
        get() = _modulationNormalized * range

    private var _effectiveValueValid = false
    private var _effectiveValue: Double = 0.0
    val effectiveValue: Double
        get() {
            if (!_effectiveValueValid) {
                val v = (normalizedValue + _modulationNormalized).coerceIn(0.0, 1.0)
                _effectiveValue = denormalize(v).coerceIn(min, max)
                _effectiveValueValid = true
            }
            return _effectiveValue
        }

    val effectiveNormalizedValue: Double
        get() = normalize(effectiveValue)

    protected open fun normalize(v: Double) = (v - min) / range
    protected open fun denormalize(v: Double) = v * range + min

    private var lastValue = _value

    protected fun notifyChange() {
        //if (abs(lastValue - _value) < .00001) return
        lastValue = _value
        if (!isOutput) {
            owner.onParameterChange(this)
        }
    }
}

class StringPlugInParameter(
    owner: PlugIn,
    key: String,
    val initialValueStr: String,
    val defaultValueStr: String,
    isOutput: Boolean = false
) : PlugInParameter(owner, key, 0.0, 0.0, 0.0, 0.0, isOutput) {

    private var _valueStr: String = initialValueStr
    override var valueStr: String
        get() = _valueStr
        set(s) {
            if (s != _valueStr) {
                _valueStr = s
                notifyChange()
            }
        }
}

class FrequencyPlugInParameter(
    owner: PlugIn,
    key: String,
    min: Double,
    max: Double,
    initialValue: Double,
    defaultValue: Double,
    isOutput: Boolean = false
) : PlugInParameter(owner, key, min, max, initialValue, defaultValue, isOutput) {

    override fun normalize(v: Double): Double {
        return v.sign * sqrt(abs((v - min) / range))
    }

    override fun denormalize(v: Double) : Double {
        return v.sign * v * v * range + min
    }
}

class SemitonesPlugInParameter(
    owner: PlugIn,
    key: String,
    min: Double,
    max: Double,
    val snap: Boolean = false, // Optional parameter with default value
    initialValue: Double,
    defaultValue: Double,
    isOutput: Boolean = false
) : PlugInParameter(owner, key, min, max, initialValue, defaultValue, isOutput) {

    override var value: Double
        get() = super.value
        set(v) {
            super.value = if (snap) v.roundToInt().toDouble() else v
        }
}

class TimingPlugInParameter(
    owner: PlugIn,
    key: String,
    min: Double,
    max: Double,
    val snap: Boolean = false, // Optional parameter with default value
    initialValue: Double,
    defaultValue: Double,
    isOutput: Boolean = false
) : PlugInParameter(owner, key, min, max, initialValue, defaultValue, isOutput) {

    override fun normalize(v: Double): Double {
        return v.sign * sqrt(abs((v - min) / range))
    }

    override fun denormalize(v: Double): Double {
        return v.sign * v * v * range + min
    }
}

open class IntPlugInParameter(
    owner: PlugIn,
    key: String,
    min: Int,
    max: Int,
    initialValue: Int,
    defaultValue: Int,
    isOutput: Boolean = false
) : PlugInParameter(owner, key, min.toDouble(), max.toDouble(), initialValue.toDouble(), defaultValue.toDouble(), isOutput) {

    override var value: Double
        get() = super.value
        set(v) {
            super.value = v.roundToInt().toDouble().coerceIn(min, max)
        }

    var intValue: Int
        get() = effectiveValue.roundToInt().toDouble().coerceIn(min, max).toInt()
        set(v) {
            value = v.toDouble()
        }
}

open class DynamicIntPlugInParameter(
    owner: PlugIn,
    key: String,
    min: Double,
    max: Double,
    initialValue: Int,
    defaultValue: Int,
    isOutput: Boolean = false
) : PlugInParameter(owner, key, min, max, initialValue.toDouble(), defaultValue.toDouble(), isOutput) {
    override var min = min
        set(value) {
            field = value
            this.value = value.coerceIn(min, max)
        }
    override var max = max
        set(value) {
            field = value
            this.value = value.coerceIn(min, max)
        }

    override val range: Double
        get() = max - min

    override var value: Double
        get() = super.value
        set(v) {
            super.value = v.roundToInt().toDouble().coerceIn(min, max)
        }

    var intValue: Int
        get() = effectiveValue.roundToInt().toDouble().coerceIn(min, max).toInt()
        set(v) {
            value = v.toDouble()
        }
}

class MidiKeyPlugInParameter(
    owner: PlugIn,
    key: String,
    min: MidiKey, // Assuming appropriate MidiKey type
    max: MidiKey,
    initialValue: Int,
    defaultValue: Int,
    isOutput: Boolean = false
) : IntPlugInParameter(owner, key, min, max, initialValue, defaultValue, isOutput) {

    var keyValue: MidiKey
        get() = intValue // Assuming conversion function to MidiKey
        set(v) {
            intValue = v
        }
}

private fun boolIntValue(v : Boolean) : Int = if (v) 1 else 0 // Helper function to convert Boolean to Int=> 1

class BoolPlugInParameter(
    owner: PlugIn,
    key: String,
    initialValue: Boolean,
    defaultValue: Boolean,
    isOutput: Boolean = false
) : IntPlugInParameter(owner, key, 0, 1, boolIntValue(initialValue), boolIntValue(defaultValue), isOutput) {

    var boolValue: Boolean
        get() = intValue != 0
        set(v) {
            intValue = if (v) 1 else 0
        }
}

class NoteTypePlugInParameter(
    owner: PlugIn,
    key: String,
    initialValue: NoteType,
    defaultValue: NoteType,
    isOutput: Boolean = false
    ) : PlugInParameter(owner, key, minNoteTypeIndex.toDouble(),
    maxNoteTypeIndex.toDouble(),
    initialValue.ordinal.toDouble(), defaultValue.ordinal.toDouble(),
    isOutput) {

    val noteValue: NoteType
    get() = NoteType.fromDouble(effectiveValue)
}

fun PlugIn.addStringParameter(
    key: String,
    initializer: List<PresetParameter>,
    defaultValueStr: String,
    isOutput: Boolean = false
): StringPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value ?: defaultValueStr
    val p = StringPlugInParameter(
        owner = this,
        key = key,
        defaultValueStr = defaultValueStr,
        initialValueStr = value,
        isOutput = isOutput
    )
    addParameter(p)
    return p
}

fun PlugIn.addDoubleParameter(
    key: String,
    minValue: Double,
    maxValue: Double,
    initializer: List<PresetParameter>,
    defaultValue: Double,
    isOutput: Boolean = false
): PlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull() ?: defaultValue
    val p = PlugInParameter(
        owner = this,
        key = key,
        min = minValue,
        max = maxValue,
        initialValue = value,
        defaultValue = defaultValue,
        isOutput = isOutput
    )
    addParameter(p)
    return p
}

fun PlugIn.addBoolParameter(
    key: String,
    initializer: List<PresetParameter>,
    defaultValue: Boolean,
    isOutput: Boolean = false
): BoolPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.let { it.toDoubleOrNull() ?: 0.0 }?.run { this >= 0.5 } ?: defaultValue
    val p = BoolPlugInParameter(
        owner = this,
        key = key,
        initialValue = value,
        defaultValue = defaultValue,
        isOutput = isOutput
    )
    addParameter(p)
    return p
}

fun PlugIn.addNoteTypeParameter(
    key: String,
    initializer: List<PresetParameter>,
    defaultValue: NoteType,
    isOutput: Boolean = false
): NoteTypePlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.let { NoteType.fromDoubleString(it) } ?: defaultValue
    val p = NoteTypePlugInParameter(
        owner = this,
        key = key,
        initialValue = value,
        defaultValue = defaultValue,
        isOutput = isOutput
    )
    addParameter(p)
    return p
}


fun PlugIn.addFrequencyParameter(
    key: String,
    minValue: Double,
    maxValue: Double,
    initializer: List<PresetParameter>,
    defaultValue: Double
): FrequencyPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull() ?: defaultValue
    val p = FrequencyPlugInParameter(
        owner = this,
        key = key,
        min = minValue,
        max = maxValue,
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

fun PlugIn.addPercentParameter(
    key: String,
    initializer: List<PresetParameter>,
    defaultValue: Double
): PlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull() ?: defaultValue
    val p = PlugInParameter(
        owner = this,
        key = key,
        min = 0.0,
        max = 1.0,
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

fun PlugIn.addVolumeParameter(
    key: String,
    minValue: Double,
    maxValue: Double,
    initializer: List<PresetParameter>,
    defaultValue: Double
): PlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull() ?: defaultValue
    val p = PlugInParameter(
        owner = this,
        key = key,
        min = minValue,
        max = maxValue,
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

fun PlugIn.addSemitoneParameter(
    key: String,
    minValue: Double,
    maxValue: Double,
    snap: Boolean,
    initializer: List<PresetParameter>,
    defaultValue: Double
): SemitonesPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull() ?: defaultValue
    val p = SemitonesPlugInParameter(
        owner = this,
        key = key,
        min = minValue,
        max = maxValue,
        snap = snap,
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

fun PlugIn.addIntParameter(
    key: String,
    minValue: Int,
    maxValue: Int,
    initializer: List<PresetParameter>,
    defaultValue: Int
): IntPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull()?.roundToInt() ?: defaultValue
    val p = IntPlugInParameter(
        owner = this,
        key = key,
        min = minValue,
        max = maxValue,
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

fun PlugIn.addDynamicIntParameter(
    key: String,
    initialMinValue: Int,
    initialMaxValue: Int,
    initializer: List<PresetParameter>,
    defaultValue: Int
): DynamicIntPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull()?.roundToInt() ?: defaultValue
    val p = DynamicIntPlugInParameter(
        owner = this,
        key = key,
        min = initialMinValue.toDouble(),
        max = initialMaxValue.toDouble(),
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

fun PlugIn.addMidiKeyParameter(
    key: String,
    minValue: MidiKey,
    maxValue: MidiKey,
    initializer: List<PresetParameter>,
    defaultValue: MidiKey
): MidiKeyPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull()?.roundToInt() ?: defaultValue
    val p = MidiKeyPlugInParameter(
        owner = this,
        key = key,
        min = minValue,
        max = maxValue,
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

fun PlugIn.addTimingParameter(
    key: String,
    minValue: Double,
    maxValue: Double,
    initializer: List<PresetParameter>,
    defaultValue: Double
): TimingPlugInParameter {
    val parameter = initializer.find {it.key == key }
    val value = parameter?.value?.toDoubleOrNull() ?: defaultValue
    val p = TimingPlugInParameter(
        owner = this,
        key = key,
        min = minValue,
        max = maxValue,
        initialValue = value,
        defaultValue = defaultValue
    )
    addParameter(p)
    return p
}

