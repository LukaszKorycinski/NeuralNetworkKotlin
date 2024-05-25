package com.example.neuralnetworkkotlin.geometry.plain3d.anim

enum class Animations(val start: Int, val end: Int, val speed: Float) {
    IDENTITY(0,0, 0.03f),
    WALK(1,8, 0.07f),
    ATTACK_CUT(8,12, 0.03f),
    ATTACK_PUSH(13,16, 0.03f),
}