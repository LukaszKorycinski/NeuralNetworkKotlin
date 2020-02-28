package com.example.neuralnetworkkotlin.geometry.collada.converter

import kotlin.math.sqrt
import kotlin.random.Random

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

    fun randomVelocity(size: Float) = Vector2f(Random.nextFloat()-0.5f, Random.nextFloat()*0.5f).normalized().mull(size)

    private fun mull(size: Float)= Vector2f (x*size, y*size)


    fun normalized(): Vector2f {
        val length =  sqrt(x*x + y*y )
        return Vector2f(x/length, y/length)
    }

    fun length(): Float {
        return sqrt(x*x + y*y )
    }

    fun distance(pos: Vector2f): Float {
        return sqrt( (x-pos.x)*(x-pos.x) + (y-pos.y)*(y-pos.y) )
    }

    constructor(a: Float) {
        this.x = a
        this.y = a
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}

