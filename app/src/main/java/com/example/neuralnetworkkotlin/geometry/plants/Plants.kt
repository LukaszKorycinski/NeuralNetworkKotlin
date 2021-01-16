package com.example.neuralnetworkkotlin.geometry

import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import java.lang.Float.min
import kotlin.math.abs
import kotlin.random.Random
import kotlin.reflect.KFunction1


class Plants {
    var plantsList = ArrayList<PlantsData>()
    var plantsGrowSpeed = 0.01f
    var addWave = 0.0f

    fun spam(collidor: Collidor) {
        for (i in 0..10) {
            addRandom(collidor)
        }
    }

    fun add(plant: PlantsData) {
        if (abs(plant.pos.x) < 5.0f && abs(plant.pos.y) < 5.0f)
            plantsList.add(plant)
    }

    fun closest(pos: Vector2f): Float {
        var closest = 10000.0f
        plantsList.forEach {
            val distance = pos.distance(it.pos)
            if (distance < closest) {
                closest = distance
            }
        }
        return closest
    }



    fun loop(collidor: Collidor){
        if( addWave>1.0f ){
            addRandom(collidor)
            addWave = 0.0f
        }else{
            addWave = addWave + plantsGrowSpeed
        }
    }

    fun addRandom(collidor: Collidor) {
        val plant = PlantsData(
            pos = Vector2f((Random.nextFloat() - 0.5f) * 3.5f, (Random.nextFloat() - 0.5f) * 3.5f),
            size = 0.5f
        )
        if (!collidor.pointColision(plant.pos)) {
            plantsList.add(plant)
        }
    }

}

class PlantsData(
    val pos: Vector2f,
    var size: Float = 0.0f
) {
    fun drawSize(): Float = min(size, 0.8f)

}