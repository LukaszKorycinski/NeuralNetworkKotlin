package com.example.neuralnetworkkotlin.ext

import javax.vecmath.Vector2f

fun Vector2f(uniform: Float): Vector2f {
    return Vector2f(uniform, uniform)
}