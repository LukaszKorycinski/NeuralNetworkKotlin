package com.example.neuralnetworkkotlin.geometry.creatures

import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.deepCopy
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.PlantsData
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import org.jbox2d.common.Vec2
import kotlin.reflect.KFunction1

class Creatures(val collidor: Collidor) {
    var speed = 2.0f
    var creaturesList = ArrayList<CreaturesData>()


    fun loop(
        onCreatureEggAdded: KFunction1<@ParameterName(name = "creature") CreaturesData, Unit>,
        seedList: ArrayList<SeedData>
    ) {
        creaturesList.forEach {
            ai(it, seedList)
            energy(onCreatureEggAdded, it)
            move(it)
        }

        creaturesList =
            creaturesList.filter { it.size > 0.1f }.filter { it.pos.y > -5.0f } as ArrayList<CreaturesData>
    }

    private fun energy(
        onCreatureEggAdded: KFunction1<@ParameterName(name = "creature") CreaturesData, Unit>,
        it: CreaturesData
    ) {
        if (it.size > 1.4f) {
            it.size = 1.0f
            val nn = it.neuralNetwork.clone()
            nn.bread()
            onCreatureEggAdded( CreaturesData(it.pos, nn, Vector2f().randomVelocity(), 0.8f).deepCopy() )
        } else {
            it.size = it.size - 0.025f * Const.step
        }
    }


private fun ai(it: CreaturesData, seedList: ArrayList<SeedData>) {
    var closestPosition = Vector2f()
    var closestL = 100.0f
    var creatureIndex = 0

    seedList.forEachIndexed { index, seed ->
        val distance = it.pos.distance(seed.pos)
        if (distance < closestL) {
            closestL = distance
            closestPosition = seed.pos
            creatureIndex = index
        }
    }

    if (closestL < 0.075f) {
        it.size = it.size + 0.5f
        seedList.removeAt(creatureIndex)
    }

    val neuralInput = ArrayList<Float>()
    neuralInput.add(it.pos.x - closestPosition.x)//closest pos x
    neuralInput.add(it.pos.y - closestPosition.y)//closest pos y

    val neuralOutput = it.neuralNetwork.inputToOutput(neuralInput)

    it.velocity.x = neuralOutput.get(0)
    if (isOnGround(it)) {
        it.velocity.y = neuralOutput.get(1)
    }
}


fun add(creature: CreaturesData) {
    creaturesList.add(creature)
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


    limitVelocity(it, 1.0f)
}

fun isOnGround(it: CreaturesData): Boolean {
    return collidor.colision(Vector2f(it.pos.x, it.pos.y - 0.05f))
}

private fun limitVelocity(it: CreaturesData, limit: Float) {
    if (it.velocity.x > limit) {
        it.velocity.x = limit
    }
    if (it.velocity.x < -limit) {
        it.velocity.x = -limit
    }
    if (it.velocity.y > limit) {
        it.velocity.y = limit
    }
    if (it.velocity.y < -limit) {
        it.velocity.y = -limit
    }
}

}


class CreaturesData(
    val pos: Vector2f,
    val neuralNetwork: NeuralNetwork,
    var velocity: Vector2f,
    var size: Float = 1.0f
) {
    fun drawSize(): Float = size

}