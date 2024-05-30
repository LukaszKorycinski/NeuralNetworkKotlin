package com.example.neuralnetworkkotlin.geometry.plain3d.anim

enum class Animations(val start: Int, val end: Int, val speed: Float, var wave: Float) {
    IDENTITY(0,0, 0.01f, 0.0f),
    WALK(1,8, 0.04f, 0.0f),
    ATTACK_CUT(8,12, 0.01f, 0.0f),
    ATTACK_PUSH(13,16, 0.016f, 0.0f),;

    fun handleWave() {
        wave += speed
        if (wave > end) {
            wave = start.toFloat()
        }
        if (wave < start) {
            wave = start.toFloat()
        }
    }

    fun copy(animation: Animations): Animations {
        this.wave = animation.wave
        return this
    }
}