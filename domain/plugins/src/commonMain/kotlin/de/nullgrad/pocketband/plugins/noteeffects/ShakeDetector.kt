package de.nullgrad.pocketband.plugins.noteeffects

import de.nullgrad.pocketband.sensors.model.SensorEvent
import de.nullgrad.pocketband.plugins.toolbox.Vector3D
import kotlin.math.absoluteValue

private class Box(
    val width: Float,
)

private class Ball(
    var position: Vector3D,
    var velocity: Vector3D
)

class ShakeDetector {
    private val shaker = Box(.05f)
    private val grain = Ball(Vector3D(), Vector3D())
    //private val wallDampingFactor = .3f
    private val dampingFactor = .9f

    private val maxIntensityInv = 1f / 1.5f

    private var lastTime = 0.0

    private var lastAcceleration = Vector3D()
    private val accFilter = .95f

    private var hitX = false

    val position: Float get() = 2 * (grain.position.x / shaker.width - .5f)

    fun processEvent(accelEvent: SensorEvent) : ShakeEvent? {

        val dt = (accelEvent.timestamp - lastTime).toFloat()
        lastTime = accelEvent.timestamp
        if (dt <= 0 || dt >= 1.0) return null

        var result : ShakeEvent? = null

        val acceleration = Vector3D(
            accFilter * accelEvent.dataX + (1f-accFilter) * lastAcceleration.x,
            accFilter * accelEvent.dataY + (1f-accFilter) * lastAcceleration.y,
            accFilter * accelEvent.dataZ + (1f-accFilter) * lastAcceleration.z
        )
        lastAcceleration = acceleration

        // Update ball's velocity based on relative acceleration
        grain.velocity = (grain.velocity - acceleration * 1.8f * dt) * dampingFactor

        // Update ball's position based on velocity
        grain.position += grain.velocity * dt

        // Handle collisions with box walls
        if (grain.position.x >= shaker.width) {
            if (!hitX) {
                result = ShakeEvent(grain.velocity.x.absoluteValue * maxIntensityInv)
                //println("hit wall 1 ${grain.velocity.x} ${result.force}")
                hitX = true
            }
            //ball.velocity.x *= -wallDampingFactor
            grain.velocity.x = 0f
            grain.position.x = shaker.width
        } else if (grain.position.x <= 0) {
            if (!hitX) {
                result = ShakeEvent(grain.velocity.x.absoluteValue * maxIntensityInv)
                //println("hit wall 2 ${grain.velocity.x} ${result.force}")
                hitX = true
            }
            //ball.velocity.x *= -wallDampingFactor
            grain.velocity.x = 0f
            grain.position.x = 0f
        }
        else {
            hitX = false
        }

        return result
    }

}
