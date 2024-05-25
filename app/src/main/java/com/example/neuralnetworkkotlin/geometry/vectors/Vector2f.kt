package com.example.neuralnetworkkotlin.geometry.vectors

import javax.vecmath.Vector2f

fun Vector2f(uniform: Float): Vector2f {
    return Vector2f(uniform, uniform)
}

operator fun Vector2f.plus(velocity: Vector2f): Vector2f {
    return Vector2f(this.x + velocity.x, this.y + velocity.y)
}