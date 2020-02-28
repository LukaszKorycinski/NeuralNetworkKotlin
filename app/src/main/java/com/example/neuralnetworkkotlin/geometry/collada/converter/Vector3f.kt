package com.example.neuralnetworkkotlin.geometry.collada.converter

import java.lang.Float.max
import java.lang.Float.min
import kotlin.random.Random

/**
 * Created by dell on 09.06.2017.
 */

class Vector3f {
    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var z: Float = 0.toFloat()

    constructor() {
        x = 0.0f
        y = 0.0f
        z = 0.0f
    }

    constructor(a: Float) {
        this.x = a
        this.y = a
        this.z = a
    }

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    operator fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    override fun toString(): String {
        return "( "+x+", "+y+", "+z+" )"
    }

    fun equals(second: Vector3f): Boolean {
        return if (Math.abs(x - second.x) < 0.00001f && Math.abs(y - second.y) < 0.00001f && Math.abs(z - second.z) < 0.00001f) true else false
    }


    fun equals(second: Vector3f, accuracy: Float): Boolean {
        return if (Math.abs(x - second.x) < accuracy && Math.abs(y - second.y) < accuracy && Math.abs(z - second.z) < accuracy) true else false
    }

    fun random() = Vector3f (Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
    fun clip(min: Float, max: Float) {
        x = max(min(x, 1.0f), 0.0f)
        y = max(min(y, 1.0f), 0.0f)
        z = max(min(z, 1.0f), 0.0f)
    }

    companion object {
        fun interpolate(
            start: Vector3f,
            end: Vector3f,
            progression: Float
        ): Vector3f {
            val x = start.x + (end.x - start.x) * progression
            val y = start.y + (end.y - start.y) * progression
            val z = start.z + (end.z - start.z) * progression
            return Vector3f(x, y, z)
        }

        fun getDistance(p1: Vector3f, p2: Vector3f): Double {
            return Math.sqrt(Math.pow((p1.x - p2.x).toDouble(), 2.0) + Math.pow((p1.y - p2.y).toDouble(), 2.0) + Math.pow((p1.z - p2.z).toDouble(), 2.0))
        }
    }
}
