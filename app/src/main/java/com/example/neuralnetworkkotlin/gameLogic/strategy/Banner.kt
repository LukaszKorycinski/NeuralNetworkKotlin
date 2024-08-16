package com.example.neuralnetworkkotlin.gameLogic.strategy

import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.geometry.vectors.rotate
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.Wave
import timber.log.Timber
import java.util.ArrayList
import java.util.UUID
import javax.vecmath.Vector2f
import kotlin.math.PI
import kotlin.math.atan2

private const val RANDOM_SOLDIERS_QTY = 8
private const val FORMATION_DENSITY = 0.5f

class Banner {

    var path: ArrayList<Vector2f> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }

    var wave = Wave(0f)
    val humans = mutableListOf<Human>()
    var position: Vector2f = Vector2f(0f)

    val teamUUID: UUID = UUID.randomUUID()
    var isSelected = false

    private var longestDistance = 0f

    fun loop(collision: Collision) {
        wave += 0.035f

        longestDistance = 0f
        var allOnFinish = true

        humans.forEach { human ->
            val currentDistance = human.distanceToDestination()
            if (currentDistance > longestDistance) {
                longestDistance = currentDistance
            }
            if (!human.isOnFinish()) {
                allOnFinish = false
            }
            if(allOnFinish){
                path.removeAt(0)
            }
        }

        humans.forEachIndexed { index, human ->
            val currentDistance = human.distanceToDestination()
            if (human.isCenturion) {
                position = human.position
            }

            val speedInFormation = currentDistance / longestDistance
            Timber.e("path: ${path.size}")
            path.firstOrNull()?.let { nextDestination ->
                Timber.e("nextDestination: $nextDestination")
                Timber.e("position: ${human.position}")
                Timber.e("distanceToDestination: ${human.distanceToDestination()}")
                human.destination = destinationInFormation(nextDestination, index)
            }

            human.centurionLoop(collision, speedInFormation)
        }
    }


    private fun destinationInFormation(
        nextDestination: Vector2f,
        humanIndex: Int,
        onInit: Boolean = false
    ): Vector2f {
        val rowSize = if (onInit) {
            RANDOM_SOLDIERS_QTY / 2
        } else {
            humans.size / 2
        }

        val xIteration = humanIndex % rowSize

        val yIteration = humanIndex / rowSize

        Timber.e("humanIndex: $humanIndex, xIteration: $xIteration, yIteration: $yIteration ")

//        val translation = Vector2f(-FORMATION_DENSITY*humans.size/4 + (FORMATION_DENSITY * xIteration), -FORMATION_DENSITY.half() + FORMATION_DENSITY * yIteration)

        val translation = Vector2f(
            (FORMATION_DENSITY * (xIteration / 2 + 1) * zeroOneToMinusPositive(xIteration % 2)),
            (FORMATION_DENSITY * yIteration)
        )
        Timber.e("translation: $translation")

        val get0 = Vector2f(0f)
        val getN = humans.firstOrNull { it.isCenturion }?.velocity ?: Vector2f(0f)

        var angle = ((
                atan2(getN.y - get0.y, getN.x - get0.x) * 180 / PI
                ) - 90.0) * PI / 180.0f

//        if (abs(angle) > PI/2) {
//            //if the angle is bigger than 90 degrees, we need to rotate the vector 180 degrees
//            //if the angle is smaller than -90 degrees, we need to rotate the vector -180 degrees
//            if (angle < -PI/2) {
//                angle -= PI
//            } else {
//                angle += PI
//            }
//        }

        val rotation = translation.rotate(angle)

        return nextDestination + rotation
    }

    fun zeroOneToMinusPositive(value: Int): Float {
        return if (value > 0) 1f else -1f
    }

    fun makeBanner(): Banner {
        for (i in 0..RANDOM_SOLDIERS_QTY / 2) {
            for (j in 0..1) {
                humans.add(
                    Human
                        .random()
                        .apply {
                            position =
                                destinationInFormation(Vector2f(0f), humans.size, onInit = true)
                            destination = position
                        }
                )
            }
        }
        humans[0].isCenturion = true
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