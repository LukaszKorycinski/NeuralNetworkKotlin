package com.example.neuralnetworkkotlin.gameLogic.strategy


import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Animations
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import com.example.neuralnetworkkotlin.geometry.vectors.minus
import com.example.neuralnetworkkotlin.geometry.vectors.normalizeOrLow
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.helpers.Collision
import timber.log.Timber
import javax.vecmath.Vector2f
import kotlin.random.Random

data class Human(
    var look: Look,
    var position: Vector2f = Vector2f(0f),
    var velocity: Vector2f = Vector2f(0f),
    var box: Vector2f = Vector2f(0.2f, 0.4f),
    var health: Int = 10,
    var wave: Float = 0f,
) {
    var destination: Vector2f = Vector2f(0f)

    fun setDestination(destination: Vector2f, completeListener: () -> Unit) {
        if (destination.distance(position) > 0.02f) {
            this.destination = destination
        } else {
            completeListener()
        }
    }

    fun loop(collision: Collision) {
        handleWave()

        //Timber.e("position: $position, destination: $destination")
        //Timber.e("distance: ${destination.distance(position)}")

        velocity = if (destination.distance(position) > 0.02f) {
            look.animation = Animations.WALK
            Vector2f(destination.x - position.x, destination.y - position.y)
                .normalizeOrLow(0.005f)
        } else {
            look.animation = Animations.IDENTITY
            Vector2f(0f)
        }

        var newPosition = position + Vector2f(velocity.x, 0f)
        if (!collision.checkCollision(newPosition)) {
            position = newPosition
        }
        newPosition = position + Vector2f(velocity.x, velocity.y)
        if (!collision.checkCollision(newPosition)) {
            position = newPosition
        }
    }


    private fun randomPosition(): Human {
        position = Vector2f((Math.random() * 1).toFloat(), (Math.random() * 3).toFloat())
        destination = position
        return this
    }

    private fun randomLook(): Human {
        look = Look.nextRandom()
        return this
    }

    fun handleWave() {
        if (wave >= look.animation.end) wave = look.animation.start.toFloat()
        if (wave < look.animation.start) wave = look.animation.start.toFloat()

        wave += look.animation.speed

        if (wave > look.animation.end) {
            wave = look.animation.start.toFloat()
        }
        if (wave < look.animation.start) {
            wave = look.animation.start.toFloat()
        }
    }

    companion object {
        fun random(): Human {
            val look = Look.nextRandom()
            return Human(look).randomPosition().randomLook()
                .apply { wave = Random.nextFloat() * look.animation.end }
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