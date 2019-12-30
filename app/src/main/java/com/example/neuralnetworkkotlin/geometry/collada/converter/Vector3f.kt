package com.example.neuralnetworkkotlin.geometry.collada.converter

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

    companion object {


        fun getDistance(p1: Vector3f, p2: Vector3f): Double {
            return Math.sqrt(Math.pow((p1.x - p2.x).toDouble(), 2.0) + Math.pow((p1.y - p2.y).toDouble(), 2.0) + Math.pow((p1.z - p2.z).toDouble(), 2.0))
        }
    }
}
