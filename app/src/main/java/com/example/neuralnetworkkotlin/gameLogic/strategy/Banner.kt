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

private const val RANDOM_SOLDIERS_QTY = 8
private const val FORMATION_DENSITY = 0.5f

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

        humans.forEachIndexed { index, human ->
            if (human.isCenturion) {
                human.destination = path.firstOrNull() ?: human.position
                if (human.isOnDestination()) {
                    path.firstOrNull()?.let { path.removeFirst() }
                }
            } else {
                human.destination = getCenturion().destination + positionInFormation(index)
            }
            human.loop(collision)
        }

        position = getCenturion().position
        directionAngle = getCenturion().angle
    }

    private fun getCenturion(): Human {
        return humans.first { it.isCenturion }
    }

    private fun positionInFormation(
        humanIndex: Int,
    ): Vector2f {
        val translation = FORMATIONS.LINE.positions[humanIndex] * FORMATION_DENSITY
        return translation.rotate(visualAngle)
    }

    private val visualAngle: Float
        get() {
            if(directionAngle > 90f.toRadians()){
                return directionAngle - 180f.toRadians()
            }
            if(directionAngle < (-90f).toRadians()){
                return directionAngle + 180f.toRadians()
            }
            return directionAngle
        }

    fun makeBanner(): Banner {
        for (i in 0..RANDOM_SOLDIERS_QTY) {
            humans.add(
                Human
                    .random()
                    .apply {
                        position = positionInFormation(i)
                        destination = position
                    }
            )
        }
        humans[0].isCenturion = true
        humans[0].angle = 2f
        directionAngle = 2f
        return this
    }

    private fun middle(): Vector2f {
        val x = humans.map { it.position.x }.average().toFloat()
        val y = humans.map { it.position.y }.average().toFloat()
        return Vector2f(x, y)
    }

}

fun MutableList<Banner>.closest(vec2: Vector2f): Banner {
    var min = Float.MAX_VALUE
    var closest = first()
    forEach {
        val distance = it.position.distance(vec2)
        if (distance < min) {
            min = distance
            closest = it
        }
    }
    return closest
}

fun MutableList<Banner>.closestIndex(vec2: Vector2f): Int {
    var min = Float.MAX_VALUE
    var index = 0
    forEachIndexed { i, banner ->
        val distance = banner.position.distance(vec2)
        if (distance < min) {
            min = distance
            index = i
        }
    }
    return index
}

enum class FORMATIONS(val positions: List<Vector2f>) {
    LINE(
        listOf(
            Vector2f(0f, 0f),
            Vector2f(0f, 1f),
            Vector2f(0f, -1f),
            Vector2f(0f, 2f),
            Vector2f(0f, -2f),
            Vector2f(0f, 3f),
            Vector2f(0f, -3f),
            Vector2f(0f, 4f),
            Vector2f(0f, -4f),
            Vector2f(0f, 5f),
            Vector2f(0f, -5f),
            Vector2f(0f, 6f),
            Vector2f(0f, -6f),
            Vector2f(0f, 7f),
        )
    ),
}