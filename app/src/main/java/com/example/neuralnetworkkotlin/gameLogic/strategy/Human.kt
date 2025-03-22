package com.example.neuralnetworkkotlin.gameLogic.strategy


import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Animations
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.vectors.angleRadians
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.distance
import com.example.neuralnetworkkotlin.geometry.vectors.minus
import com.example.neuralnetworkkotlin.geometry.vectors.normalizeOrLow
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.geometry.vectors.rotate
import com.example.neuralnetworkkotlin.geometry.vectors.times
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.Vector3f

import com.example.neuralnetworkkotlin.helpers.Collision
import java.util.UUID
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.math.PI
import kotlin.random.Random

private const val VELOCITY_MULTIPLIER = 0.02f
private const val ANGLE_CHANGE_SPEED = 0.5f
private const val PRECISION = 0.02f

data class Human(
    var look: Look,
    var position: Vector2f = Vector2f(0f),
    var height: Float = 1f,
    var velocity: Vector2f = Vector2f(0f),
    var box: Vector2f = Vector2f(0f),
    var health: Int = 10,
    var angle: Float = 0f,
    var wave: Float = 0f,
    var waveWalk: Float = 0f,
    var waveSword: Float = 0f,
    var isCenturion: Boolean = false,
) {

    val uuid: UUID = UUID.randomUUID()
    var destination: Vector2f = Vector2f(0f)

    fun isOnDestination() = isOnPlace(destination)
    private fun isOnPlace(place: Vector2f) = place.distance(position) < PRECISION

    fun distanceToDestination() = destination.distance(position)

    fun loop(collision: Collision, centurionDistance: Float = 1f) {
        handleWave()

        calculateVelocity(centurionDistance)

        move(collision)
        this.height = collision.terrain.getHeight(position.x, position.y)
    }


    private fun calculateVelocity(centurionDistance: Float) {
        look.direction = if (velocity.x > 0) Direction.RIGHT else Direction.LEFT
        look.animation = if (velocity.length() > 0) Animations.WALK else Animations.IDENTITY
        if (isOnDestination()) {
            velocity = Vector2f(0f, 0f)
            return
        }

        val idealVelocity = destination - position
        val idealAngle = idealVelocity.angleRadians()

        if (idealAngle.isNaN()) {
            velocity = Vector2f(0f, 0f)
            return
        }
        val angleDiff = idealAngle - angle
        val step = ANGLE_CHANGE_SPEED

        angle = if (isCenturion) {
            if (angleDiff > step) angle + step else if (angleDiff < -step) angle - step else idealAngle
        } else {
            idealAngle
        }

        velocity = Vector2f(1f, 0f).rotate(angle).normalizeOrLow(VELOCITY_MULTIPLIER)
  //      Timber.e("velocity 1: $velocity")

        if (centurionDistance > PRECISION)
            velocity = velocity * (distanceToDestination() / centurionDistance)

//        Timber.e("distanceToDestination: ${distanceToDestination()}")
//        Timber.e("centurionDistance: $centurionDistance")
//        Timber.e("multipler: ${distanceToDestination() / centurionDistance}")
//        Timber.e("velocity 2: $velocity")
        //velocity = idealVelocity.normalizeOrLow(VELOCITY_MULTIPLIER)
        //angle = velocity.angle(Vector2f(1f, 0f))
//        val direction = destination - position
//        val normalizedDirection = direction.normalizeOrLow(VELOCITY_MULTIPLIER)
//        velocity = normalizedDirection
    }


    private fun move(collision: Collision) {
        val newPosition = position + Vector2f(velocity.x, 0f)

        if (collision.checkCollision(newPosition, uuid)) {
            newPosition.x = position.x
        }
        newPosition.y = position.y + velocity.y
        if (collision.checkCollision(newPosition, uuid)) {
            newPosition.y = position.y
        }

        position = position + velocity// newPosition
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
        //if (wave >= look.animation.end) wave = look.animation.start.toFloat()
        //if (wave < look.animation.start) wave = look.animation.start.toFloat()

        if (waveWalk > 2 * PI) waveWalk = 0f
        waveWalk += look.animation.speedWalk

        //if(waveSword>2*PI) waveSword = 0f
        //waveSword += look.animation.speedSword


//        if (wave > look.animation.end) {
//            wave = look.animation.start.toFloat()
//        }
//        if (wave < look.animation.start) {
//            wave = look.animation.start.toFloat()
//        }
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
    var animation: Animations = Animations.ATTACK_CUT,
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