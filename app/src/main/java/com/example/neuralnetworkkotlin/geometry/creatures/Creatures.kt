package com.example.neuralnetworkkotlin.geometry.creatures

import android.util.Log
import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.collada.converter.Line
import com.example.neuralnetworkkotlin.geometry.collada.converter.Triangle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import com.example.neuralnetworkkotlin.helpers.Collision

import java.io.Serializable
import kotlin.random.Random
import kotlin.reflect.KFunction1

class Creatures(val collidor: Collidor) {
    var speed = 0.5f
    var creaturesList = ArrayList<CreaturesData>()
    var creaturesListToAdd = ArrayList<CreaturesData>()

    var lifeEnergyCost = 0.05f
    var energyFromEat = 0.3f
    var mutantRatio = 20
    var cornerSpeedMultificaier = 1.0f


    fun loop(
        onCreatureEggAdded: KFunction1<@ParameterName(name = "creature") CreaturesData, Unit>,
        seedList: ArrayList<SeedData>,
        coli: Collision
    ) {
        creaturesList.forEach {
            ai(it, seedList, coli)
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


            color.x = it.color.x + 0.02f * isMutant * (Random.nextFloat()-0.5f)
            color.y = it.color.y + 0.02f * isMutant * (Random.nextFloat()-0.5f)
            color.z = it.color.z + 0.02f * isMutant * (Random.nextFloat()-0.5f)


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



    private fun ai(it: CreaturesData, seedList: ArrayList<SeedData>, coli: Collision) {
        var closestPosition = Vector2f()
        var closestL = 1000.0f
        var closestSeedIndex = 0


//        val triangle1 = Triangle(it.pos, Vector2f(it.pos.x+0.2f, it.pos.y+0.2f), Vector2f(it.pos.x-0.2f, it.pos.y+0.2f))
//        val triangle2 = Triangle(it.pos, Vector2f(it.pos.x+0.2f, it.pos.y+0.2f), Vector2f(it.pos.x+0.2f, it.pos.y-0.2f))
//        val triangle3 = Triangle(it.pos, Vector2f(it.pos.x+0.2f, it.pos.y-0.2f), Vector2f(it.pos.x-0.2f, it.pos.y-0.2f))

        val eyeSign = it.pos + (Vector2f(it.velocity.x, it.velocity.y).normalized().mull(0.7f))

        val eyePos = it.pos + (it.velocity.normalized().mull(0.025f))


        val line1 = Line(eyePos, eyeSign )
        val line2 = Line(eyePos, eyeSign.rotate(Math.toRadians(22.5)) )
        val line3 = Line(eyePos, eyeSign.rotate(Math.toRadians(-22.5)) )
//        val line4 = Line(it.pos, eyeSign.rotate(Math.toRadians(45.0)) )
//        val line5 = Line(it.pos, eyeSign.rotate(Math.toRadians(-45.0)) )


        it.glowing = false

        val eyes: ArrayList<Float> = arrayListOf(0.0f, 0.0f, 0.0f)


        val startTime = System.currentTimeMillis()

        seedList.forEachIndexed { index, seed ->
            val distance = it.pos.distance(seed.pos)
            if (distance < closestL) {
                closestL = distance
                closestPosition = seed.pos
                closestSeedIndex = index

                eyes[0] = coli.pointLineColision(seed.pos, line1, it.pos)
                eyes[1] = coli.pointLineColision(seed.pos, line2, it.pos)
                eyes[2] = coli.pointLineColision(seed.pos, line3, it.pos)

            }



        }

        val endTime = System.currentTimeMillis()

        Log.e("opt", "lag = " + (endTime - startTime))


        if (closestL < 0.025f) {
            it.size = it.size + energyFromEat
            seedList.removeAt(closestSeedIndex)
        }



        val neuralInput = ArrayList<Float>()
        neuralInput.add( eyes[0] )
        neuralInput.add( eyes[1] )
        neuralInput.add( eyes[2] )

        //neuralInput.add(it.pos.x - closestPosition.y )//closest pos y
        //neuralInput.add(if(isOnGround(it))1.0f else 0.0f)

        val neuralOutput = it.neuralNetwork.inputToOutput(neuralInput)

        val degree = (neuralOutput.get(0) - neuralOutput.get(1)) * Const.step * 250.0f*cornerSpeedMultificaier

        it.velocity = it.velocity.rotate(Math.toRadians(degree.toDouble())).normalized()
//        it.velocity.x = neuralOutput.get(0)
//        it.velocity.y = neuralOutput.get(1)

    }


    fun add(creature: CreaturesData) {
        creaturesListToAdd.add(creature)
    }


    private fun move(it: CreaturesData) {

        //limitVelocity(it, 0.5f, 0.5f)

        val newPosition = Vector2f(
            it.pos.x + it.velocity.x * Const.step * speed,
            it.pos.y + it.velocity.y * Const.step * speed
        )

        if (!collidor.colision(Vector2f(newPosition.x, it.pos.y))) {
            it.pos.x = newPosition.x
        } else {
            it.velocity.x = -it.velocity.x// * 0.2f
            it.velocity.y = it.velocity.y// * 0.75f
        }

        if (!collidor.colision(Vector2f(it.pos.x, newPosition.y))) {
            it.pos.y = newPosition.y
        } else {
            it.velocity.x = it.velocity.x// * 0.75f
            it.velocity.y = -it.velocity.y// * 0.2f
        }

    }



//    private fun limitVelocity(it: CreaturesData, limitX: Float, limitY: Float) {
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
//    }

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
    var color: Vector3f,
    var glowing: Boolean = false
) : Serializable {
    fun drawSize(): Float = size

    fun getAngle(): Float {
        return Math.atan2(velocity.x.toDouble(), velocity.y.toDouble()).toFloat()*180f/3.1415f
    }

}