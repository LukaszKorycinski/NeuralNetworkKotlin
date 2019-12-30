package com.example.neuralnetworkkotlin.geometry.collada.converter

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
}

