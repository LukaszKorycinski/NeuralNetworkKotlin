package com.example.neuralnetworkkotlin.gameLogic.strategy


import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Animations
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import javax.vecmath.Vector2f

data class Human(
    var look: Look,
    var position: Vector2f = Vector2f(0f),
    var velocity: Vector2f = Vector2f(0f),
    var box: Vector2f = Vector2f(0.2f, 0.4f),
    var health: Int = 10
) {





    private fun randomPosition(): Human {
        position = Vector2f((Math.random() * 3).toFloat(), (Math.random() * 8).toFloat())
        return this
    }

    private fun randomLook(): Human {
        look = Look.nextRandom()
        return this
    }

    companion object {
        fun random(): Human {
            val look = Look.nextRandom()
            return Human(look).randomPosition().randomLook()
        }
    }
}

data class Variant(
    val head: Int,
    val beard: Int,
    val skinColor: Float,
) {
    companion object {
        fun random() = Variant(
            head = (Math.random() * 4).toInt(),
            beard = (Math.random() * 4).toInt(),
            skinColor = (0.125f - Math.random() * 0.25f).toFloat()
        )
    }
}

data class Look(
    var direction: Direction = Direction.RIGHT,
    var animation: Animations = Animations.IDENTITY,
    var variant: Variant,
) {
    companion object {
        fun nextRandom(): Look {
            return Look(
                direction = Direction.values().random(),
                animation = Animations.values().random(),
                variant = Variant.random(),
            )
        }
    }
}


enum class Direction(val scaleX: Float) {
    LEFT(-1f),
    RIGHT(1f)
}