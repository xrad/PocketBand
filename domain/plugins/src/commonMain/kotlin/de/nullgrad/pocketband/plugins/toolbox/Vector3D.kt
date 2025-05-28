package de.nullgrad.pocketband.plugins.toolbox

data class Vector3D(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
    operator fun plus(other: Vector3D): Vector3D =
        Vector3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3D): Vector3D =
        Vector3D(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float): Vector3D =
        Vector3D(x * scalar, y * scalar, z * scalar)
}