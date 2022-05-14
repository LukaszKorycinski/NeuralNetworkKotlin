package com.example.neuralnetworkkotlin.geometry.creatures

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.ext.nextDoubleFromRange
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.collada.converter.Line
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import com.example.neuralnetworkkotlin.helpers.Collision
import com.google.gson.Gson
import java.io.Serializable
import kotlin.random.Random
import kotlin.reflect.KFunction1


class Creatures(val collidor: Collidor) {
    var speed = 0.5f
    var creaturesList = ArrayList<CreaturesData>()
    var creaturesListToAdd = ArrayList<CreaturesData>()

    var lifeEnergyCost = 0.025f
    var speedCost = 0.025f
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

            if(!controlMode)
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
        parent: CreaturesData
    ) {
        if (parent.size > 1.4f) {
            parent.size = parent.size - 0.4f
            val nn = parent.genome.neuralNetwork.clone()
            val isMutant = nn.bread(mutantRatio)

            val color = Vector3f()


            color.x = parent.genome.color.x + 0.14f * isMutant * (Random.nextFloat()-0.5f)
            color.y = parent.genome.color.y + 0.14f * isMutant * (Random.nextFloat()-0.5f)
            color.z = parent.genome.color.z + 0.14f * isMutant * (Random.nextFloat()-0.5f)


            color.colorClip(0.0f, 1.0f)

            val eyeAngle = parent.genome.eyeAngle + if((0..mutantRatio).random()==1) Random.nextDoubleFromRange(-5.0, 5.0) else 0.0


            onCreatureEggAdded(
                CreaturesData(
                    pos = Vector2f(parent.pos.x, parent.pos.y),
                    genome = Genome(color, nn.clone(), eyeAngle),
                    velocity = Vector2f().randomVelocity(1.0f),
                    eye = Vector3f()
                )
            )
        } else {
            parent.size = parent.size - lifeEnergyCost * Const.step //* maxOf(parent.speed, 0.5f)
        }
    }



    private fun ai(currentCreature: CreaturesData, seedList: ArrayList<SeedData>, coli: Collision) {
        var closestPosition = Vector2f()

        var closestSeedL = 100000.0f
        var closestSeedIndex = 0

        var closestMateL = 100000.0f
        var closestMateIndex = 0


//        val triangle1 = Triangle(it.pos, Vector2f(it.pos.x+0.2f, it.pos.y+0.2f), Vector2f(it.pos.x-0.2f, it.pos.y+0.2f))
//        val triangle2 = Triangle(it.pos, Vector2f(it.pos.x+0.2f, it.pos.y+0.2f), Vector2f(it.pos.x+0.2f, it.pos.y-0.2f))
//        val triangle3 = Triangle(it.pos, Vector2f(it.pos.x+0.2f, it.pos.y-0.2f), Vector2f(it.pos.x-0.2f, it.pos.y-0.2f))

        val eyeSign = currentCreature.eyeSign()

        val eyePos = currentCreature.pos //+ (currentCreature.velocity.normalized().mull(0.025f))


        val line1 = Line(eyePos, eyeSign )
        val line2 = Line(eyePos,eyePos+(eyeSign-eyePos).rotate(Math.toRadians(currentCreature.genome.eyeAngle)) )
        val line3 = Line(eyePos,eyePos+(eyeSign-eyePos).rotate(Math.toRadians(-currentCreature.genome.eyeAngle)) )
//        val line4 = Line(it.pos, eyeSign.rotate(Math.toRadians(45.0)) )
//        val line5 = Line(it.pos, eyeSign.rotate(Math.toRadians(-45.0)) )


        currentCreature.glowing = false

        //val eyes: ArrayList<Float> = arrayListOf(0.0f, 0.0f, 0.0f)


        val startTime = System.currentTimeMillis()

        currentCreature.eye.x = 1.0f
        seedList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line1)
            if (distance < closestSeedL) {
                closestSeedL = distance
                currentCreature.eye.x = distance//coli.pointLineColision(seed.pos, line1)
            }
        }

        closestSeedL = 100000.0f
        currentCreature.eye.y = 1.0f
        seedList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line2)
            if (distance < closestSeedL) {
                closestSeedL = distance
                currentCreature.eye.y = distance//coli.pointLineColision(seed.pos, line1)
            }
        }

        closestSeedL = 100000.0f
        currentCreature.eye.z = 1.0f
        seedList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line3)
            if (distance < closestSeedL) {
                closestSeedL = distance
                currentCreature.eye.z = distance//coli.pointLineColision(seed.pos, line1)
            }
        }



        closestPosition = Vector2f()
        closestSeedL = 100000.0f
        closestSeedIndex = 0
        seedList.forEachIndexed { index, seed ->
            val distanceEat = currentCreature.pos.distance(seed.pos)//każde oko sprawdza kolizję i jeśli gdzieś jest to 1

            if (distanceEat < closestSeedL) {
                closestSeedL = distanceEat
                closestPosition = seed.pos
                closestSeedIndex = index
            }
        }
        creaturesList.forEachIndexed { index, mate ->
            val distanceSex = currentCreature.pos.distance(mate.pos)//każde oko sprawdza kolizję i jeśli gdzieś jest to 1

            if (distanceSex < closestSeedL) {
                closestSeedL = distanceSex
                closestPosition = mate.pos
                closestMateIndex = index
            }
        }

        val endTime = System.currentTimeMillis()

        //Log.e("opt", "lag = " + (endTime - startTime))


        if (closestSeedL < 0.075f) {//jedzenie
            currentCreature.size = currentCreature.size + energyFromEat
            seedList.removeAt(closestSeedIndex)
        }



        val neuralInput = ArrayList<Float>()
        neuralInput.add( 1.0f-currentCreature.eye.y )
        neuralInput.add( 1.0f-currentCreature.eye.x )
        neuralInput.add( 1.0f-currentCreature.eye.z )
        neuralInput.add( 1.0f/*currentCreature.size*/ )
        //neuralInput.add(it.pos.x - closestPosition.y )//closest pos y
        //neuralInput.add(if(isOnGround(it))1.0f else 0.0f)

        val neuralOutput = currentCreature.genome.neuralNetwork.inputToOutput(neuralInput)
        Log.e("eyeAngle",currentCreature.genome.eyeAngle.toString())

        currentCreature.speed = minOf(neuralOutput.get(2), 1.0f)

        val degree = (neuralOutput.get(0) - neuralOutput.get(1)) * Const.step * 150.0f*cornerSpeedMultificaier



        if(!controlMode)
        currentCreature.velocity = currentCreature.velocity.rotate(Math.toRadians(degree.toDouble())).normalized()

        //currentCreature.size = currentCreature.size - neuralOutput.get(2) * speedCost * Const.step


//        it.velocity.x = neuralOutput.get(0)
//        it.velocity.y = neuralOutput.get(1)

    }

    var controlMode = false
    fun controllCreature(controls: Vector2f){
        if(creaturesList.size>0) {
            creaturesList.get(0).pos = creaturesList.get(0).pos + controls.mull(0.1f)
            controlMode = true
        }
    }


    fun add(creature: CreaturesData) {
        creaturesListToAdd.add(creature)
    }


    private fun move(curentCreature: CreaturesData) {

        //limitVelocity(it, 0.5f, 0.5f)
        val moveVector = curentCreature.velocity.mull(maxOf(curentCreature.speed, 0.1f))

        val newPosition = Vector2f(
            curentCreature.pos.x + moveVector.x * Const.step * speed,
            curentCreature.pos.y + moveVector.y * Const.step * speed
        )

        if (!collidor.colision(Vector2f(newPosition.x, curentCreature.pos.y))) {
            curentCreature.pos.x = newPosition.x
        } else {
            curentCreature.velocity.x = -curentCreature.velocity.x// * 0.2f
            curentCreature.velocity.y = curentCreature.velocity.y// * 0.75f
        }

        if (!collidor.colision(Vector2f(curentCreature.pos.x, newPosition.y))) {
            curentCreature.pos.y = newPosition.y
        } else {
            curentCreature.velocity.x = curentCreature.velocity.x// * 0.75f
            curentCreature.velocity.y = -curentCreature.velocity.y// * 0.2f
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

    fun saveNN(activity: Activity) {
        val sharedPreferences: SharedPreferences = activity.getSharedPreferences("MySharedPref", MODE_PRIVATE)

        val myEdit = sharedPreferences.edit()
        val gson = Gson()

        creaturesList.sortedByDescending { it.size }.firstOrNull()?.let{
            val json = gson.toJson(  it  )
            myEdit.putString("savedNN", json)
            myEdit.apply()
        }
    }

    fun loadNN(activity: Activity) {
        val sharedPreferences: SharedPreferences = activity.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val gson = Gson()
        val json: String? = sharedPreferences.getString("savedNN", null)
        creaturesList.add(gson.fromJson(json, CreaturesData::class.java))
    }

}




class CreaturesData(
    var pos: Vector2f,
    var velocity: Vector2f,
    var size: Float = 1.0f,
    var genome: Genome,
    var eye: Vector3f,
    var glowing: Boolean = false,
    var speed: Float = 1.0f,
    var wave: Float = 0.0f
) : Serializable {
    fun drawSize(): Float = size

    fun getAngle(): Float {
        return Math.atan2(velocity.x.toDouble(), velocity.y.toDouble()).toFloat()*180f/3.1415f
    }

    fun eyeSign(): Vector2f{
        return pos + (Vector2f(velocity.x, velocity.y).normalized().mull(0.4f))
    }

    fun eyeSignBack(): Vector2f{
        return pos - (Vector2f(velocity.x, velocity.y).normalized().mull(0.4f))
    }
}

class Genome (
    var color: Vector3f,
    var neuralNetwork: NeuralNetwork,
    var eyeAngle: Double
)
