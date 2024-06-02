package com.example.neuralnetworkkotlin.gameLogic.strategy

import java.util.UUID
import javax.vecmath.Vector2f
import kotlin.math.PI

private const val RANDOM_SOLDIERS_QTY = 8

class Banner {

    var position = Vector2f()
    var wave = 0f
    val humans = mutableListOf<Human>()
    val teamUUID: UUID = UUID.randomUUID()

    fun makeBanner(): Banner {
        for (i in 0..RANDOM_SOLDIERS_QTY / 2) {
            for (j in 0..1) {
                humans.add(
                    Human
                        .random()
                        .apply {
                            position = Vector2f(0.4f * i, 0.4f * j)
                        }
                )
            }
        }
        this.position = middle(humans)
        return this
    }

    private fun middle(humans: MutableList<Human>): Vector2f {
        val x = humans.map { it.position.x }.average().toFloat()
        val y = humans.map { it.position.y }.average().toFloat()
        return Vector2f(x, y)
    }

    fun handleWave() {
        wave += 0.035f
        if(wave > PI * 2) wave = 0f
    }

}