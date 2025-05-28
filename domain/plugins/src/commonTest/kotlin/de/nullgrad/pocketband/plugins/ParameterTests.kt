package de.nullgrad.pocketband.plugins

import de.nullgrad.pocketband.di.LOCATOR
import de.nullgrad.pocketband.presets.model.PresetParameter
import de.nullgrad.pocketband.test.mocks.MockPlugInInstrument
import de.nullgrad.pocketband.test.util.runTestDi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ParameterTests {

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
        LOCATOR.clear()
    }

    @Test
    fun initializersTest() {
        val plugIn = MockPlugInInstrument(1, listOf(
            PresetParameter(MockPlugInInstrument.KEY_PARAM_1, "2.0"),
            PresetParameter(MockPlugInInstrument.KEY_PARAM_2, "1"),
            PresetParameter(MockPlugInInstrument.KEY_PARAM_3, ".1"),
            PresetParameter(MockPlugInInstrument.KEY_PARAM_4, "World"),
            PresetParameter(MockPlugInInstrument.KEY_PARAM_5, "1"),
        ))
        assertEquals(2.0, plugIn.param1.value, .001)
        assertEquals(1, plugIn.param2.intValue)
        assertEquals(.1, plugIn.param3.value, .001)
        assertEquals("World", plugIn.param4.valueStr)
        assertEquals(true, plugIn.param5.boolValue)
    }

    @Test
    fun parameterTest() = runTestDi {
        val plugIn = MockPlugInInstrument(1, emptyList())
        plugIn.listenerCalledCounter = 0
        plugIn.param1.value += .1
        assertEquals(1, plugIn.listenerCalledCounter)
        assertEquals(plugIn.param1, plugIn.lastParameterUpdate)
    }

    @Test
    fun parameterNormalization() = runTestDi {
        val plugIn = MockPlugInInstrument(1, emptyList())

        // param1 is configured to be between 0 and 10

        // should be coerced to min
        plugIn.param1.normalizedValue = -0.5
        assertEquals(0.0, plugIn.param1.effectiveValue, 0.001)

        plugIn.param1.normalizedValue = 0.0
        assertEquals(0.0, plugIn.param1.effectiveValue, 0.001)

        plugIn.param1.normalizedValue = 0.5
        assertEquals(5.0, plugIn.param1.effectiveValue, 0.001)

        plugIn.param1.normalizedValue = 1.0
        assertEquals(10.0, plugIn.param1.effectiveValue, 0.001)

        // should be coerced to max 
        plugIn.param1.normalizedValue = 1.2
        assertEquals(10.0, plugIn.param1.effectiveValue, 0.001)
    }

    @Test
    fun parameterNormalizationReverse() = runTestDi {
        val plugIn = MockPlugInInstrument(1, emptyList())
        // aram1 is configured to be between 0 and 10 

        // should be coerced to min 
        plugIn.param1.value = -1.0
        assertEquals(0.0, plugIn.param1.normalizedValue, 0.001)

        plugIn.param1.value = 0.0
        assertEquals(0.0, plugIn.param1.normalizedValue, 0.001)

        plugIn.param1.value = 5.0
        assertEquals(0.5, plugIn.param1.normalizedValue, 0.001)

        plugIn.param1.value = 10.0
        assertEquals(1.0, plugIn.param1.normalizedValue, 0.001)

        // should be coerced to max 
        plugIn.param1.value = 11.0
        assertEquals(1.0, plugIn.param1.normalizedValue, 0.001)
    }

    @Test
    fun parameterValueStr() = runTestDi {
        val plugIn = MockPlugInInstrument(1, emptyList())
        // aram1 is configured to be between 0 and 10 

        plugIn.param1.value = 1.5
        assertEquals("1.5", plugIn.param1.valueStr)
    }

    @Test
    fun parameterModulation() = runTestDi {
        val plugIn = MockPlugInInstrument(1, emptyList())
        // aram1 is configured to be between 0 and 10 

        plugIn.param1.normalizedValue = 0.2
        plugIn.param1.modulationNormalized = 0.1

        assertEquals(0.2, plugIn.param1.normalizedValue, 0.001)
        assertEquals(0.3, plugIn.param1.effectiveNormalizedValue, 0.001)
        assertEquals(3.0, plugIn.param1.effectiveValue, 0.001)

        // should be coerced to min 
        plugIn.param1.modulationNormalized = -0.3
        assertEquals(0.0, plugIn.param1.effectiveNormalizedValue, 0.001)

        // should be coerced to max 
        plugIn.param1.modulationNormalized = 0.9
        assertEquals(1.0, plugIn.param1.effectiveNormalizedValue, 0.001)
    }
}
