package com.example.neuralnetworkkotlin.gameLogic.strategy


import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Animations
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import com.example.neuralnetworkkotlin.geometry.vectors.normalize
import com.example.neuralnetworkkotlin.geometry.vectors.normalizeOrLow
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.geometry.vectors.rotate
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.RADIANS_90
import timber.log.Timber
import java.util.UUID
import javax.vecmath.Vector2f
import kotlin.random.Random

const val VELOCITY_MULTIPLIER = 0.02f

data class Human(
    var look: Look,
    var position: Vector2f = Vector2f(0f),
    var velocity: Vector2f = Vector2f(0f),
    var box: Vector2f = Vector2f(0.2f, 0.4f),
    var health: Int = 10,
    var wave: Float = 0f,
    var isCenturion: Boolean = false,
) {
    val uuid: UUID = UUID.randomUUID()
    var destination: Vector2f = Vector2f(0f)
        set(value) {
            if (isOnPlace(value)) {
                if(!isOnFinish() ){
                    field = destination
                }
            } else {
                field = destination
            }
        }

    fun isOnFinish() = isOnPlace(destination)
    private fun isOnPlace(place: Vector2f) = place.distance(position) < 0.02f

    fun distanceToDestination() = destination.distance(position)

//    fun setDestination(destination: Vector2f) {
//        if (isOnPlace(destination)) {
//            if(!isOnFinish() ){
//                this.destination = destination
//            }
//        } else {
//            this.destination = destination
//        }
//    }

    fun centurionLoop(collision: Collision, speedInFormation: Float) {
        handleWave()
        velocity = calculateVelocity(speedInFormation)
        Timber.e("velocity: ${velocity}")
        move(collision)
    }

    private fun calculateVelocity(speedInFormation: Float) = if (!isOnFinish()) {
        look.direction = if (destination.x > position.x) Direction.RIGHT else Direction.LEFT
        look.animation = Animations.WALK
        Vector2f(destination.x - position.x, destination.y - position.y)
            .normalizeOrLow(VELOCITY_MULTIPLIER)
    } else {
        look.animation = Animations.IDENTITY
        Vector2f(0f)
    }

    private fun move(collision: Collision, rerun: Boolean = true): Boolean {
        val newPosition = position + Vector2f(velocity.x, 0f)

        var coliCount = 0

//        if (collision.checkCollision(newPosition, uuid)) {
//            newPosition.x = position.x
//            coliCount++
//        }
//        newPosition.y = position.y + velocity.y
//        if (collision.checkCollision(newPosition, uuid)) {
//            newPosition.y = position.y
//            coliCount++
//        }
        newPosition.normalize(desireL = VELOCITY_MULTIPLIER)
        position = newPosition

        if (coliCount == 2 && rerun) {
            velocity = velocity.rotate(RADIANS_90)
            move(collision, false)
            return false
        }
        return true
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
    LEFT(1f),
    RIGHT(-1f)
}