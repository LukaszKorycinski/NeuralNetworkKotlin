package com.example.neuralnetworkkotlin.gameLogic.strategy

import com.example.neuralnetworkkotlin.geometry.vectors.distance
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.Wave
import timber.log.Timber
import java.util.ArrayList
import java.util.UUID
import javax.vecmath.Vector2f
import kotlin.math.PI

private const val RANDOM_SOLDIERS_QTY = 8

class Banner {

    var path: ArrayList<Vector2f> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }

    var wave = Wave(0f)
    val humans = mutableListOf<Human>()
    val position : Vector2f
        get() = middle()

    val teamUUID: UUID = UUID.randomUUID()
    var isSelected = false


    fun loop(collision: Collision) {
        wave += 0.035f

        humans.forEachIndexed { index, human ->
            path.firstOrNull()?.let{ nextDestination ->
                human.setDestination(destinationInFormation(nextDestination, index)) {
                    path.removeAt(0)
                }
            }

            human.loop(collision)
        }
    }


    fun destinationInFormation(nextDestination: Vector2f, humanIndex: Int): Vector2f {

        val xIteration = humanIndex % (humans.size / 2)
        val yIteration = humanIndex / (humans.size / 2)

        return nextDestination + Vector2f(0.4f * xIteration, 0.4f * yIteration)
    }


    fun makeBanner(): Banner {
        for (i in 0..RANDOM_SOLDIERS_QTY / 2) {
            for (j in 0..1) {
                humans.add(
                    Human
                        .random()
                        .apply {
                            position = Vector2f(0.4f * i, 0.4f * j)
                            destination = Vector2f(0.4f * i, 0.4f * j)
                        }
                )
            }
        }
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