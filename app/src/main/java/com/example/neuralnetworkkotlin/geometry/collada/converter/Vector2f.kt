package com.example.neuralnetworkkotlin.geometry.collada.converter

import kotlin.math.sqrt

/**
 * Created by dell on 09.06.2017.
 */

class Vector2f {
    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()

    constructor() {
        x = 0.0f
        y = 0.0f
    }

    fun equals(second: Vector2f): Boolean {
        return if (Math.abs(x - second.x) < 0.0001f && Math.abs(y - second.y) < 0.0001f) true else false
    }

    constructor(a: Float) {
        this.x = a
        this.y = a
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun normalize(desireL: Float = 1.0f):Vector2f{
        val currentL = sqrt(x * x + y * y)
        return Vector2f((x/currentL)*desireL, (y/currentL*desireL))
    }

    override fun toString(): String {
        return (x.toString() + ", "+y.toString())
    }

    fun rotate(radians: Double): Vector2f{
        val ca = Math.cos(radians).toFloat()
        val sa = Math.sin(radians).toFloat()

        return Vector2f(ca*x - sa*y, sa*x + ca*y)
    }

    fun length(): Float {
        return sqrt(x * x + y * y)
    }

    fun normalizeOrLow(desireL: Float = 1.0f): Vector2f {
        val currentL = sqrt(x * x + y * y)
        return if(currentL>desireL)  normalize(desireL) else this
    }

    fun distance(p2: Vector2f): Double {
        return Math.sqrt(Math.pow((x - p2.x).toDouble(), 2.0) + Math.pow((y - p2.y).toDouble(), 2.0))
    }
}

