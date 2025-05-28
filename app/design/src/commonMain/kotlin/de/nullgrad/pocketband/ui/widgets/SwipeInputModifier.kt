package de.nullgrad.pocketband.ui.widgets

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue

interface SwipeInputInteraction : Interaction {
    class LongPressed() : SwipeInputInteraction
    class StartSwiping() : SwipeInputInteraction
    class SwipeUpdate(start: StartSwiping) : SwipeInputInteraction
    class StopSwiping(start: StartSwiping) : SwipeInputInteraction
    class DismissContextMenu() : SwipeInputInteraction
}

fun Modifier.swipeInput(
    value: Float,
    onValueChanged: (Float) -> Unit,
    onDoubleTap: () -> Unit,
    focusRequester: FocusRequester? = null,
    interactionSource: MutableInteractionSource? = null,
) = this then SwipeInputElement(
    value,
    onValueChanged,
    onDoubleTap,
    focusRequester,
    interactionSource,
)

private data class SwipeInputElement(
    val value: Float,
    val onValueChanged: (Float) -> Unit,
    val onDoubleTap: () -> Unit,
    val focusRequester: FocusRequester? = null,
    val interactionSource: MutableInteractionSource? = null,
) : ModifierNodeElement<SwipeInputNode>() {

    override fun create() = SwipeInputNode(
        value,
        onValueChanged,
        onDoubleTap,
        focusRequester = focusRequester,
        interactionSource = interactionSource
    )

    override fun update(node: SwipeInputNode) {
        node.value = value
        node.onValueChanged = onValueChanged
        node.onDoubleTap = onDoubleTap
        node.focusRequester = focusRequester
        node.interactionSource = interactionSource
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "swipeInput"
        properties["onValueChanged"] = onValueChanged
        properties["onDoubleTap"] = onDoubleTap
    }
}

private class SwipeInputNode(
    var value: Float,
    var onValueChanged: (Float) -> Unit,
    var onDoubleTap: () -> Unit,
    var range: ClosedFloatingPointRange<Float> = 0f..1f,
    var steps: Int = 0,
    var focusRequester: FocusRequester? = null,
    var interactionSource: MutableInteractionSource? = null,
) : PointerInputModifierNode,
    CompositionLocalConsumerModifierNode,
    Modifier.Node() {

    // when slop has been reached and swiping is in progress
    private var swiping = false
    // position of first tap down
    private var pressOffset = Offset.Zero
    private var doubleTapTimer = 0L
    private var longPressJob: Job? = null
    private var swipeInteraction: SwipeInputInteraction.StartSwiping? = null

    var doubleTapMinTimeMillis: Long = 200
    var doubleTapTimeoutMillis: Long = 600
    var longPressTimeoutMillis: Long = 400

    private fun updateMouseConfig() {
        val viewConfiguration = currentValueOf(LocalViewConfiguration)
        doubleTapMinTimeMillis = viewConfiguration.doubleTapMinTimeMillis
        doubleTapTimeoutMillis = viewConfiguration.doubleTapTimeoutMillis
        longPressTimeoutMillis = viewConfiguration.longPressTimeoutMillis
    }

    override fun onAttach() {
        super.onAttach()
        updateMouseConfig()
    }

    override fun onViewConfigurationChange() {
        super.onViewConfigurationChange()
        updateMouseConfig()
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        if (pass != PointerEventPass.Main) return
        pointerEvent.changes.forEach { d ->
            when (pointerEvent.type) {
                PointerEventType.Press -> {
                    pressOffset = d.position
                    resetDirection()
                    swiping = false
                    focusRequester?.requestFocus()
                    d.consume()
                    longPressJob = coroutineScope.launch {
                        delay(longPressTimeoutMillis)
                        interactionSource?.tryEmit(SwipeInputInteraction.LongPressed())
                    }
                }

                PointerEventType.Move -> {
                    if (swiping) {
                        // when use is resting on a value for a while, return
                        // to waiting for the initial slop. This is supposed
                        // to help when making fine adjustments of the value.
                        val dt = d.uptimeMillis - d.previousUptimeMillis
                        swiping = dt < 200
                    }
                    if (!swiping) {
                        val slopOffset = d.position - pressOffset
                        if (slopOffset.getDistance() > 10) {
                            longPressJob?.cancel()
                            swiping = true
                            swipeInteraction = SwipeInputInteraction.StartSwiping().also {
                                interactionSource?.tryEmit(it)
                            }
                            //d.consume()
                        }
                    }
                    if (swiping) {
                        dispatchRawDelta(d.positionChange(), bounds)
                        d.consume()
                        swipeInteraction?.let {
                            interactionSource?.tryEmit(SwipeInputInteraction.SwipeUpdate(it))
                        }
                    }
                }

                PointerEventType.Release -> {
                    longPressJob?.cancel()
                    longPressJob = null
                    doubleTapTimer = d.uptimeMillis - doubleTapTimer
                    if (doubleTapTimer in doubleTapMinTimeMillis..doubleTapTimeoutMillis) {
                        onDoubleTap()
                    }
                    doubleTapTimer = d.uptimeMillis
                    swipeInteraction?.let {
                        interactionSource?.tryEmit(SwipeInputInteraction.StopSwiping(it))
                    }
                    swiping = false
                    swipeInteraction = null
                }
            }
        }
    }

    private var direction: Orientation? = null

    private fun resetDirection() {
        direction = null
    }

    override fun onCancelPointerInput() {
        longPressJob?.cancel()
        longPressJob = null
    }

    private fun dispatchRawDelta(delta: Offset, bounds: IntSize) {
        if (direction == null) {
            direction = if (delta.x.absoluteValue > delta.y.absoluteValue)
                Orientation.Horizontal else Orientation.Vertical
        }
        val d: Float = when (direction) {
            Orientation.Horizontal -> delta.x
            Orientation.Vertical -> -delta.y / 10
            else -> 0f
        }
        val rawValue = value + d / bounds.width
        val coercedValue = rawValue.coerceIn(range)
        val snappedValue = snapValueToTick(coercedValue, range.start, range.endInclusive)
        onValueChanged(snappedValue)
    }

    private val tickFractions =
        if (steps == 0) floatArrayOf() else FloatArray(steps + 1) { it / steps.toFloat() }

    private fun snapValueToTick(
        current: Float,
        minPx: Float,
        maxPx: Float
    ): Float {
        // target is a closest anchor to the `current`, if exists
        return tickFractions
            .minByOrNull { abs(lerp(minPx, maxPx, it) - current) }
            ?.run { lerp(minPx, maxPx, this) }
            ?: current
    }
}
