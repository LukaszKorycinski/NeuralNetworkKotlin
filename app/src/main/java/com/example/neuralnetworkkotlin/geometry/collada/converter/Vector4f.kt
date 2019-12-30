package com.example.neuralnetworkkotlin.geometry.collada.converter

class Vector4f {

    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var z: Float = 0.toFloat()
    var w: Float = 0.toFloat()

    constructor() {
        x = 0.0f
        y = 0.0f
        z = 0.0f
        w = 0.0f
    }

    constructor(a: Float) {
        this.x = a
        this.y = a
        this.z = a
        this.w = w
    }

    constructor(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    constructor(vector3f: Vector3f, v: Float) {
        this.x = vector3f.x
        this.y = vector3f.y
        this.z = vector3f.z
        this.w = v
    }

    fun equals(second: Vector4f): Boolean? {
        return if (Math.abs(x - second.x) < 0.00001f && Math.abs(y - second.y) < 0.00001f && Math.abs(z - second.z) < 0.00001f && Math.abs(w - second.w) < 0.00001f) true else false
    }

}
