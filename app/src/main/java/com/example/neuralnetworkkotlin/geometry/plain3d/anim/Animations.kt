package com.example.neuralnetworkkotlin.geometry.plain3d.anim

enum class Animations(
    val start: Int,
    val end: Int,
    val speed: Float,
    val speedWalk: Float,
    val speedSword: Float,
) {
    IDENTITY(13, 15, 0.3f, 0.0f, 0.0f),
    WALK(1, 5, 0.3f, 0.3f, 0.0f),
    ATTACK_CUT(7, 10, 0.01f, 0.0f, 0.1f),
    ATTACK_PUSH(10, 13, 0.016f, 0.0f, 0.16f), ;
}