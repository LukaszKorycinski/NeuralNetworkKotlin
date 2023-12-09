package com.example.neuralnetworkkotlin.geometry.vectors

class Vector3i {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0

    fun equals(second: Vector3i): Boolean? {
        return if (x == second.x && y == second.y && z == second.z) true else false
    }

    constructor() {
        x = 0
        y = 0
        z = 0
    }

    constructor(a: Int) {
        this.x = a
        this.y = a
        this.z = a
    }

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }
}