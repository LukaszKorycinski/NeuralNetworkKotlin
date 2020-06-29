package com.example.neuralnetworkkotlin.geometry.creatures

import android.util.Log
import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.PlantsData
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import org.jbox2d.common.Vec2
import java.io.Serializable
import java.lang.Math.random
import kotlin.random.Random
import kotlin.reflect.KFunction1

class Creatures(val collidor: Collidor) {
    var speed = 2.0f
    var creaturesList = ArrayList<CreaturesData>()
    var creaturesListToAdd = ArrayList<CreaturesData>()

    var lifeEnergyCost = 0.05f
    var energyFromEat = 0.3f
    var mutantRatio = 20


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
            creaturesList.filter { it.size > 0.2f }.filter { it.pos.y > -5.0f } as ArrayList<CreaturesData>

        if (creaturesListToAdd.isNotEmpty()) {
            creaturesList.addAll(creaturesListToAdd)
            creaturesListToAdd.clear()
        }
    }

    private fun energy(
        onCreatureEggAdded: KFunction1<@ParameterName(name = "creature") CreaturesData, Unit>,
        it: CreaturesData
    ) {
        if (it.size > 1.4f) {
            it.size = it.size - 0.4f
            val nn = it.neuralNetwork.clone()
            val isMutant = nn.bread(mutantRatio)

            val color = Vector3f()

            color.x = it.color.x + 0.04f * isMutant
            color.y = it.color.y - 0.01f * isMutant
            color.z = it.color.z - 0.01f * isMutant


            color.clip(0.0f, 1.0f)

            onCreatureEggAdded(
                CreaturesData(
                    Vector2f(it.pos.x, it.pos.y),
                    nn.clone(),
                    Vector2f().randomVelocity(1.0f),
                    0.8f,
                    color
                )
            )
        } else {
            it.size = it.size - lifeEnergyCost * Const.step
        }
    }


    private fun ai(it: CreaturesData, seedList: ArrayList<SeedData>) {
        var closestPosition = Vector2f()
        var closestL = 1000.0f
        var closestSeedIndex = 0

        seedList.forEachIndexed { index, seed ->
            val distance = it.pos.distance(seed.pos)
            if (distance < closestL) {
                closestL = distance
                closestPosition = seed.pos
                closestSeedIndex = index
            }
        }

        if (closestL < 0.075f) {
            it.size = it.size + energyFromEat
            seedList.removeAt(closestSeedIndex)
        }

        val neuralInput = ArrayList<Float>()
        neuralInput.add(closestPosition.x - it.pos.x)//closest pos x
        neuralInput.add(closestPosition.y - it.pos.y)//closest pos y

        val neuralOutput = it.neuralNetwork.inputToOutput(neuralInput)

//        if (isOnGround(it)) {
//            Log.e("velocity", "x "+ +neuralOutput.get(0)+" y "+neuralOutput.get(1))
            it.velocity.x = neuralOutput.get(0)
            it.velocity.y = neuralOutput.get(1)
//        }
    }


    fun add(creature: CreaturesData) {
        creaturesListToAdd.add(creature)
    }


    private fun move(it: CreaturesData) {

        limitVelocity(it, 0.15f, 0.15f)

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
        it.velocity.y = it.velocity.y + Const.gravity * Const.step*100.0f//gravity
    }

    fun isOnGround(it: CreaturesData): Boolean {
        return collidor.colision(Vector2f(it.pos.x, it.pos.y - 0.05f))
    }

    private fun limitVelocity(it: CreaturesData, limitX: Float, limitY: Float) {
        it.velocity = it.velocity.normalized(limitX)
//        if (it.velocity.x > limitX) {
//            it.velocity.x = limitX
//        }
//        if (it.velocity.x < -limitX) {
//            it.velocity.x = -limitX
//        }
//        if (it.velocity.y > limitY) {
//            it.velocity.y = limitY
//        }
//        if (it.velocity.y < -limitY) {
//            it.velocity.y = -limitY
//        }
    }

    fun saveNN() {

    }

    fun loadNN() {

    }

}


class CreaturesData(
    val pos: Vector2f,
    val neuralNetwork: NeuralNetwork,
    var velocity: Vector2f,
    var size: Float = 1.0f,
    var color: Vector3f
) : Serializable {
    fun drawSize(): Float = size

}