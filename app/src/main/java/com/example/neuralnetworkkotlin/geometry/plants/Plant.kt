package com.example.neuralnetworkkotlin.geometry

import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import java.lang.Float.min
import kotlin.random.Random
import kotlin.reflect.KFunction1


class Plant {
    var plantsList = ArrayList<PlantsData>()


    fun loop(onSeedAdded: KFunction1<@ParameterName(name = "seed") SeedData, Unit>) {
        plantsList.forEach {
            it.size = it.size + 0.05f * Const.step

            if(it.size>1.2f){
                onSeedAdded(SeedData(Vector2f(it.pos.x+0.025f, it.pos.y+0.15f+Random.nextFloat()*0.1f), Vector2f().randomVelocity(), 0.0f))
                onSeedAdded(SeedData(Vector2f(it.pos.x-0.025f, it.pos.y+0.15f+Random.nextFloat()*0.1f), Vector2f().randomVelocity(), 0.0f))
                it.size = 0.8f
            }
        }

        //plantsList = plantsList.filter { it.size<=0.8f } as ArrayList<PlantsData>
    }

    fun add(plant: PlantsData) {
        plantsList.add(plant)
    }

    fun closest(pos: Vector2f): Float {
        var closest = 10000.0f
        plantsList.forEach{
            val distance = pos.distance(it.pos)
            if(distance<closest){
                closest = distance
            }
        }
        return closest
    }

}

class PlantsData(
    val pos: Vector2f,
    var size: Float = 0.0f
) {
    fun drawSize():Float = min(size, 0.8f)

}