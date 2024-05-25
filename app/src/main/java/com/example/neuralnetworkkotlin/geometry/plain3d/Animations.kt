package com.example.neuralnetworkkotlin.geometry.plain3d

enum class Animations(val start: Int, val end: Int, val speed: Float) {
    IDENTITY(0,0, 0.03f),
    WALK(1,8, 0.07f),
    ATTACK(8,12, 0.03f),
}