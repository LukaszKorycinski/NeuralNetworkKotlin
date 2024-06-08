package com.example.neuralnetworkkotlin.geometry.vectors

import javax.vecmath.Vector2f
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Vector2f(uniform: Float): Vector2f {
    return Vector2f(uniform, uniform)
}

fun Vector2f.copy(x: Float = this.x, y: Float = this.y): Vector2f {
    return Vector2f(x, y)
}

operator fun Vector2f.plus(velocity: Vector2f): Vector2f {
    return Vector2f(this.x + velocity.x, this.y + velocity.y)
}

operator fun Vector2f.minus(velocity: Vector2f): Vector2f {
    return Vector2f(this.x - velocity.x, this.y - velocity.y)
}

fun Vector2f.rotate(radians: Double): Vector2f {
    val cos = cos(radians).toFloat()
    val sin = sin(radians).toFloat()

    return Vector2f(cos * x - sin * y, sin * x + cos * y)
}

fun Vector2f.normalizeOrLow(desireL: Float = 1.0f): Vector2f {
    val currentL = sqrt(x * x + y * y)
    return if(currentL>desireL) normalize(desireL) else this
}

fun Vector2f.normalize(desireL: Float = 1.0f): Vector2f{
    val currentL = sqrt(x * x + y * y)
    return Vector2f((x/currentL)*desireL, (y/currentL*desireL))
}