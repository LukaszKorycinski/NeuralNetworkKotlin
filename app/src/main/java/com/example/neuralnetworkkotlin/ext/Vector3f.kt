package com.example.neuralnetworkkotlin.ext

import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.math.pow
import kotlin.math.sqrt

fun Vector3f.distance (vector: Vector3f): Float =
    sqrt(
        (this.x - vector.x).pow(2)
                +
                (this.y - vector.y).pow(2)
                +
                (this.z - vector.z).pow(2))

fun Vector2f.distance (vector: Vector2f): Float =
    sqrt(
        (this.x - vector.x).pow(2)
                +
                (this.y - vector.y).pow(2))