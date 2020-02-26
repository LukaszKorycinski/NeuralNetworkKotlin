package com.example.neuralnetworkkotlin.geometry.creatures

import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.geometry.PlantsData
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import org.jbox2d.common.Vec2
import kotlin.reflect.KFunction1

class Creatures(val collidor: Collidor) {
    var speed = 2.0f
    var creaturesList = ArrayList<CreaturesData>()


    fun loop(onCreatureAdded: KFunction1<@ParameterName(name = "creature") CreaturesData, Unit>, seedList: ArrayList<SeedData>) {
        creaturesList.forEach {
            val creature = it
            ai(creature, seedList)
            move(creature)
        }
    }

    private fun ai(it: CreaturesData, seedList: ArrayList<SeedData>) {
        var closestPosition = Vec2()
        var closestL = 100.0f
        val creature = it

        seedList.forEach {
            val distance = creature.pos.distance(it.pos)
            if( distance<closestL ){
                closestL = distance
            }
        }
    }


    fun add(plant: CreaturesData) {
        creaturesList.add(plant)
    }


    private fun move(it: CreaturesData) {

        val newPosition = Vector2f(
            it.pos.x + it.velocity.x * Const.step * speed,
            it.pos.y + it.velocity.y * Const.step * speed
        )

        if (!collidor.colision(Vector2f(newPosition.x, it.pos.y))) {
            it.pos.x = newPosition.x
        } else {
            it.velocity.x = -it.velocity.x * 0.2f
            it.velocity.y = it.velocity.y * 0.75f
        }

        if (!collidor.colision(Vector2f(it.pos.x, newPosition.y))) {
            it.pos.y = newPosition.y
        } else {
            it.velocity.x = it.velocity.x * 0.75f
            it.velocity.y = -it.velocity.y * 0.2f
        }
        it.velocity.y = it.velocity.y + Const.gravity//gravity
    }

}


class CreaturesData(
    val pos: Vector2f,
    var velocity: Vector2f,
    var size: Float = 0.0f
) {
    fun drawSize(): Float = java.lang.Float.min(size, 0.8f)

}