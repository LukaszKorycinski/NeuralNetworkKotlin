package com.example.neuralnetworkkotlin.geometry.plain3d.anim

enum class Animations(val start: Int, val end: Int, val speed: Float) {
    IDENTITY(13,15, 0.02f),
    WALK(1,5, 0.3f),
    ATTACK_CUT(7,10, 0.01f),
    ATTACK_PUSH(10,13, 0.016f),;
}