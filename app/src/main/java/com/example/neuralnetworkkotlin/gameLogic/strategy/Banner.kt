package com.example.neuralnetworkkotlin.gameLogic.strategy

import com.example.neuralnetworkkotlin.ext.toRadians
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.geometry.vectors.rotate
import com.example.neuralnetworkkotlin.geometry.vectors.times
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.Wave
import timber.log.Timber
import java.util.ArrayList
import java.util.UUID
import javax.vecmath.Vector2f
import kotlin.math.abs

private const val RANDOM_SOLDIERS_QTY = 14
private const val FORMATION_DENSITY = 0.35f

class Banner(
    var wave: Wave = Wave(0f),
    val humans: MutableList<Human> = mutableListOf<Human>(),
    var position: Vector2f = Vector2f(0f),
    var directionAngle: Float = 0F,
    val teamUUID: UUID = UUID.randomUUID(),
    var isSelected: Boolean = false
) {

    var path: ArrayList<Vector2f> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }

    fun loop(collision: Collision) {
        wave += 0.035f

        var everyoneInDestination = true
        humans.forEach {
            if (!it.isOnDestination()) {
                everyoneInDestination = false
            }
        }

        val centurion = getCenturion()


        humans.forEachIndexed { index, human ->
            if (human.isCenturion) {
                human.destination = path.firstOrNull() ?: human.position
                if (everyoneInDestination) {
                    path.firstOrNull()?.let { path.removeFirst() }
                }
            } else {
                human.destination =
                    getCenturion().destination + positionInFormation(index) + noise(index)
            }
            human.loop(collision, centurionDistance = centurion.distanceToDestination())
        }

        //val centurion = getCenturion()
        position = centurion.position
        directionAngle = centurion.angle
    }

    private fun getCenturion(): Human {
        return humans.first { it.isCenturion }
    }

    private fun noise(index: Int, factor: Float = .1f): Vector2f {
        return Vector2f(
            factor * hash(index),
            factor * hash(index + 1)
        )
    }

    private fun hash(value: Int): Float {
        val prime = 2654435761L
        var hash = value * prime
        hash = hash xor (hash ushr 16)
        return (hash and 0xFFFF) / 0xFFFF.toFloat()
    }

    private fun positionInFormation(
        humanIndex: Int,
    ): Vector2f {
        val translation = FORMATIONS.BOX.positions[humanIndex] * FORMATION_DENSITY
        return translation.rotate(visualAngle)
    }

    private val visualAngle: Float
        get() {
            if (directionAngle > 90f.toRadians()) {
                return directionAngle - 180f.toRadians()
            }
            if (directionAngle < (-90f).toRadians()) {
                return directionAngle + 180f.toRadians()
            }
            return directionAngle
        }

    fun makeBanner(pos: Vector2f = Vector2f()): Banner {
        for (i in 0..RANDOM_SOLDIERS_QTY) {
            humans.add(
                Human
                    .random()
                    .apply {
                        position = positionInFormation(i) + pos
                        destination = position
                    }
            )
        }
        humans[0].isCenturion = true
        humans[0].angle = 2f
        directionAngle = 2f
        return this
    }
}

fun MutableList<Banner>.closestIndex(vec2: Vector2f): Pair<Int, Float> {
    var min = Float.MAX_VALUE
    var index = 0
    var distanceToClosestUnit = 0f

    forEachIndexed { i, banner ->
        distanceToClosestUnit = banner.humans.minOf { it.position.distance(vec2) }
        if (distanceToClosestUnit < min) {
            min = distanceToClosestUnit
            index = i
        }
    }
    return index to min
}

enum class FORMATIONS(val positions: List<Vector2f>) {
    LINE(
        listOf(
            Vector2f(0f, 0f),//0
            Vector2f(0f, 1f),//1
            Vector2f(0f, -1f),//2
            Vector2f(0f, 2f),//3
            Vector2f(0f, -2f),//4
            Vector2f(0f, 3f),//5
            Vector2f(0f, -3f),//6
            Vector2f(0f, 4f),//7
            Vector2f(0f, -4f),//8
            Vector2f(0f, 5f),//9
            Vector2f(0f, -5f),//10
            Vector2f(0f, 6f),//11
            Vector2f(0f, -6f),//12
            Vector2f(0f, 7f),//13
            Vector2f(0f, -7f),//14
            Vector2f(0f, 8f),//15
            Vector2f(0f, -8f), //16
        )
    ),
    BOX(
        listOf(
            Vector2f(0f, 0f),//0
            Vector2f(0f, 1f),//1
            Vector2f(0f, -1f),//2
            Vector2f(1f, 0f),//3
            Vector2f(-1f, 0f),//4
            Vector2f(1f, 1f),//5
            Vector2f(-1f, -1f),//6
            Vector2f(1f, -1f),//7
            Vector2f(-1f, 1f),//8
            Vector2f(2f, 0f),//9
            Vector2f(-2f, 0f),//10
            Vector2f(2f, 1f),//11
            Vector2f(-2f, -1f),//12
            Vector2f(2f, -1f),//13
            Vector2f(-2f, 1f),//14
            Vector2f(0f, 2f),//15
            Vector2f(0f, -2f),//16

        )
    ),
}