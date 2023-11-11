package com.example.neuralnetworkkotlin.geometry.creatures

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.ext.nextDoubleFromRange
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.Particle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Line
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import com.example.neuralnetworkkotlin.helpers.Collision
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.Integer.max
import java.lang.reflect.Type
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import kotlin.reflect.KFunction1


class Creatures(val collidor: Collidor) {
    var speed = 0.5f
    var creaturesList = ArrayList<CreaturesData>()
    var creaturesListToAdd = ArrayList<CreaturesData>()

    final val INITIAL_LIFE_ENERGY_COST = 0.025f
    final val INITIA_ENERGY_FRON_EAT = 0.3f
    final val INITIA_SPEED_COST = 0.025f

    var lifeEnergyCost = INITIAL_LIFE_ENERGY_COST
    var speedCost = INITIA_SPEED_COST
    var energyFromEat = INITIA_ENERGY_FRON_EAT
    var mutantRatio = 20
    var cornerSpeedMultificaier = 200.0f

    var energyFromEatCompensator = 1.0f

    fun loop(
        onCreatureEggAdded: KFunction1<@ParameterName(name = "creature") CreaturesData, Unit>,
        seedList: ArrayList<SeedData>,
        coli: Collision
    ): List<Particle> {
        //sinusInput = sin(wave)
        var particlesToAdd: List<Particle> = emptyList()
        val eatedCreaturesIds: ArrayList<Int> = arrayListOf();
        creaturesList.forEach { creature ->
//            if(creature.genome.eatMeat){
//                val idsToDelete = aiMeatEater(creature,
//                    ArrayList(creaturesList
//                        .filter { !it.genome.eatMeat}
//                        .filter { it.size < creature.size }
//                        .filter { !eatedCreaturesIds.contains(it.id) }
//                        .toMutableList()), coli)
//                eatedCreaturesIds.addAll(
//                    idsToDelete
//                )
//            }else{
                aiPlantsEater(creature, seedList//,
//                    ArrayList(creaturesList
//                        .filter { it.genome.eatMeat}
//                        .filter { it.size > creature.size }
//                        .toMutableList())
                    ,coli)
//            }

            energy(onCreatureEggAdded, creature)

            if(!controlMode)
                move(creature)
        }

        particlesToAdd = eatedCreaturesIds.map { id ->
            Particle(creaturesList.first { it.id == id }.pos, 0.0f)
        }


        creaturesList =
            creaturesList
                .filter { it.size > 0.2f }
                .filter { it.size < 2.0f }
                .filter { it.pos.y > -5.0f }
                .filter {
                    !eatedCreaturesIds.contains(it.id)
            } as ArrayList<CreaturesData>

        if (creaturesListToAdd.isNotEmpty()) {

            val tmpList = creaturesListToAdd.map {
                it.genome.kidsQty = min(it.genome.kidsQty, 4)
                it
            }

            creaturesList.addAll(creaturesListToAdd)
            creaturesListToAdd.clear()
        }

        return particlesToAdd
    }




    private fun energy(
        onCreatureEggAdded: KFunction1<@ParameterName(name = "creature") CreaturesData, Unit>,
        parent: CreaturesData
    ) {
        if (parent.size > parent.genome.breedSize) {
            parent.size = parent.size -  parent.genome.kidSize * parent.genome.kidsQty//0.4f

            if(parent.size < 0.0f){
                return
            }

            val nn = parent.genome.neuralNetwork.clone()
            var isMutant = nn.bread(mutantRatio)

            val isMutant_eyeAngle = (0..mutantRatio).random()==1
            val isMutant_breedSize = (0..mutantRatio).random()==1

            val eyeAngle = parent.genome.eyeAngle + if(isMutant_eyeAngle) Random.nextDoubleFromRange(-3.0, 3.0) else 0.0
            val breedSize = parent.genome.breedSize + if(isMutant_breedSize) Random.nextDoubleFromRange(-0.1, 0.1).toFloat() else 0.0f
            val kidSize = parent.genome.kidSize + if((0..mutantRatio).random()==1) Random.nextDoubleFromRange(-0.1, 0.1).toFloat() else 0.0f
            var kidsQty = parent.genome.kidsQty + min(if((0..mutantRatio).random()==1) {if(Random.nextBoolean())1 else -1} else 0, 4)
            kidsQty = max(1, kidsQty)

            if(isMutant_eyeAngle) isMutant++
            if(isMutant_breedSize) isMutant++

            val color = Vector3f()
            color.x = parent.genome.color.x + 0.14f * isMutant * (Random.nextFloat()-0.5f)
            color.y = parent.genome.color.y + 0.14f * isMutant * (Random.nextFloat()-0.5f)
            color.z = parent.genome.color.z + 0.14f * isMutant * (Random.nextFloat()-0.5f)
            color.colorClip(0.0f, 1.0f)

            for(i in 0..kidsQty-1) {
                onCreatureEggAdded(
                    CreaturesData(
                        pos = Vector2f(parent.pos.x, parent.pos.y),
                        genome = Genome(color, nn.clone(), eyeAngle, breedSize, kidSize, kidsQty, parent.genome.eatMeat),
                        velocity = Vector2f().randomVelocity(1.0f),
                        eye = Vector3f(),
                        generation = parent.generation + 1,
                        size = parent.genome.kidSize * 0.6f,
                    )
                )
            }
        } else {
            parent.size = parent.size - lifeEnergyCost * Const.step * parent.size //* maxOf(parent.speed*2.0f, 1.0f)
            //Log.e("parent.size",""+parent.size+" "+lifeEnergyCost * Const.step * maxOf(parent.speed*2.0f, 1.0f)+" l "+lifeEnergyCost + " s "+Const.step + " sp "+maxOf(parent.speed*2.0f, 1.0f))
        }
    }


    private fun aiMeatEater(currentCreature: CreaturesData, meatList: ArrayList<CreaturesData>, coli: Collision): ArrayList<Int> {

        var eatedCreaturesIds = arrayListOf<Int>()

        var closestSeedL = 1.0f

        val eyeSign = currentCreature.eyeSign()

        val eyePos = currentCreature.pos //+ (currentCreature.velocity.normalized().mull(0.025f))

        val line1 = Line(eyePos, eyeSign )
        val line2 = Line(eyePos,eyePos+(eyeSign-eyePos).rotate(Math.toRadians(currentCreature.genome.eyeAngle)) )
        val line3 = Line(eyePos,eyePos+(eyeSign-eyePos).rotate(Math.toRadians(-currentCreature.genome.eyeAngle)) )

        currentCreature.glowing = false

        currentCreature.eye.x = 1.0f
        meatList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line1)
            if ( distance < closestSeedL && distance < 0.5f) {
                closestSeedL = distance

            }
        }
        currentCreature.eye.x = closestSeedL//coli.pointLineColision(seed.pos, line1)

        closestSeedL = 1.0f
        currentCreature.eye.y = 1.0f
        meatList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line2)
            if ( distance < closestSeedL &&  distance < 0.5f) {
                closestSeedL = distance
            }
        }
        currentCreature.eye.y = closestSeedL//coli.pointLineColision(seed.pos, line1)

        closestSeedL = 1.0f
        var closestSeedLGlobal = 10000.0f
        var closestSeedIndex = -1
        //var closestPosition = Vector2f()
        currentCreature.eye.z = 1.0f
        meatList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line3)
            if ( distance < closestSeedL && distance < 0.5f) {
                closestSeedL = distance
            }

            if( currentCreature.pos.distance(seed.pos) < closestSeedLGlobal){
                closestSeedLGlobal = currentCreature.pos.distance(seed.pos)
                //closestPosition = seed.pos
                closestSeedIndex = index
            }
        }
        currentCreature.eye.z = closestSeedL//coli.pointLineColision(seed.pos, line1)

        if (closestSeedIndex > -1 && meatList.get(closestSeedIndex).pos.distance(eyePos) < 0.04f) {//jedzenie
            if(meatList.size>closestSeedIndex){
                currentCreature.size = currentCreature.size + meatList.get(closestSeedIndex).size * 0.95f
                eatedCreaturesIds.add(meatList.get(closestSeedIndex).id)
                //meatList.removeAt(closestSeedIndex)
            }
        }

        val neuralInput = ArrayList<Float>()
        neuralInput.add( 1.0f-currentCreature.eye.y )
        neuralInput.add( 1.0f-currentCreature.eye.x )
        neuralInput.add( 1.0f-currentCreature.eye.z )
        neuralInput.add( 0.0f )
        neuralInput.add( 0.0f )
        neuralInput.add( 0.0f )
        neuralInput.add( currentCreature.size )
        neuralInput.add( sin(currentCreature.wave) )

        val neuralOutput = currentCreature.genome.neuralNetwork.inputToOutput(neuralInput)

        currentCreature.speed = maxOf(minOf(neuralOutput.get(2), 1.0f), 0.0f)

        val nnOutputRotate = if((abs(neuralOutput.get(0) - neuralOutput.get(1)))<0.001f) 0.0f else neuralOutput.get(0) - neuralOutput.get(1)
        val degree = (nnOutputRotate) * Const.step * cornerSpeedMultificaier * currentCreature.speed
        currentCreature.velocity = currentCreature.velocity.rotate(Math.toRadians(degree.toDouble())).normalized()

        return eatedCreaturesIds
    }

    private fun aiPlantsEater(currentCreature: CreaturesData, seedList: ArrayList<SeedData>, /*predatorList: List<CreaturesData>,*/ coli: Collision) {

        var closestSeedL = 1.0f
        val eyeSign = currentCreature.eyeSign()

        val eyePos = currentCreature.pos //+ (currentCreature.velocity.normalized().mull(0.025f))

        val line1 = Line(eyePos, eyeSign )
        val line2 = Line(eyePos,eyePos+(eyeSign-eyePos).rotate(Math.toRadians(currentCreature.genome.eyeAngle)) )
        val line3 = Line(eyePos,eyePos+(eyeSign-eyePos).rotate(Math.toRadians(-currentCreature.genome.eyeAngle)) )

        var isPredator1 = false
        var isPredator2 = false
        var isPredator3 = false

        currentCreature.glowing = false

        currentCreature.eye.x = 1.0f
        seedList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line1)
            if ( distance < closestSeedL && distance < 0.5f) {
                closestSeedL = distance
            }
        }
//        predatorList.forEachIndexed { index, seed ->
//            val distance = coli.pointLineColision(seed.pos, line1)
//            if ( distance < closestSeedL && distance < 0.5f) {
//                closestSeedL = distance
//                isPredator1 = true
//            }
//        }
        currentCreature.eye.x = closestSeedL//coli.pointLineColision(seed.pos, line1)


        closestSeedL = 1.0f
        currentCreature.eye.y = 1.0f
        seedList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line2)
            if ( distance < closestSeedL && distance < 0.5f) {
                closestSeedL = distance
            }
        }
//        predatorList.forEachIndexed { index, seed ->
//            val distance = coli.pointLineColision(seed.pos, line2)
//            if ( distance < closestSeedL && distance < 0.5f) {
//                closestSeedL = distance
//                isPredator2 = true
//            }
//        }
        currentCreature.eye.y = closestSeedL//coli.pointLineColision(seed.pos, line1)




        closestSeedL = 1.0f
        var closestSeedLGlobal = 10000.0f
        var closestSeedIndex = -1
        currentCreature.eye.z = 1.0f
        seedList.forEachIndexed { index, seed ->
            val distance = coli.pointLineColision(seed.pos, line3)
            if ( distance < closestSeedL && distance < 0.5f) {
                closestSeedL = distance
            }
            if( currentCreature.pos.distance(seed.pos) < closestSeedLGlobal){
                closestSeedLGlobal = currentCreature.pos.distance(seed.pos)
                //closestPosition = seed.pos
                closestSeedIndex = index
            }
        }
//        predatorList.forEachIndexed { index, seed ->
//            val distance = coli.pointLineColision(seed.pos, line3)
//            if ( distance < closestSeedL && distance < 0.5f) {
//                closestSeedL = distance
//                isPredator3 = true
//            }
//        }
        currentCreature.eye.z = closestSeedL//coli.pointLineColision(seed.pos, line1)

        if (closestSeedIndex > -1 && seedList.get(closestSeedIndex).pos.distance(eyePos) < 0.04f*currentCreature.size) {//jedzenie
            if(seedList.size>closestSeedIndex){
                currentCreature.size = currentCreature.size + energyFromEat * energyFromEatCompensator
                seedList.removeAt(closestSeedIndex)
            }
        }

        val neuralInput = ArrayList<Float>()
        neuralInput.add( 1.0f-currentCreature.eye.y )
        neuralInput.add( 1.0f-currentCreature.eye.x )
        neuralInput.add( 1.0f-currentCreature.eye.z )
        neuralInput.add( if(isPredator2) 1.0f else 0.0f )
        neuralInput.add( if(isPredator1) 1.0f else 0.0f )
        neuralInput.add( if(isPredator3) 1.0f else 0.0f )
        neuralInput.add( currentCreature.size )
        neuralInput.add( sin(currentCreature.wave) )

        val neuralOutput = currentCreature.genome.neuralNetwork.inputToOutput(neuralInput)

        currentCreature.speed = maxOf(minOf(neuralOutput.get(2), 1.0f), 0.0f)

        val nnOutputRotate = if((abs(neuralOutput.get(0) - neuralOutput.get(1)))<0.001f) 0.0f else neuralOutput.get(0) - neuralOutput.get(1)
        val degree = (nnOutputRotate) * Const.step * cornerSpeedMultificaier * currentCreature.speed
        currentCreature.velocity = currentCreature.velocity.rotate(Math.toRadians(degree.toDouble())).normalized()
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
        val moveVector = curentCreature.velocity.mull(maxOf(curentCreature.speed, 0.0f))

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

        //creaturesList.sortedByDescending { it.size }.firstOrNull()?.let{
            val json = gson.toJson(  creaturesList  )
        Log.e("json nn save", json)
            myEdit.putString("savedNN", json)
            myEdit.apply()
        //}
    }

    fun loadNN(activity: Activity) {
        val sharedPreferences: SharedPreferences = activity.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<CreaturesData>>(){}.type
        val json: String? = sharedPreferences.getString("savedNN", null)
        Log.e("json nn load", json?:"")
        Log.e("json nn load2", gson.fromJson<ArrayList<CreaturesData>>(json, type).toString())
        Log.e("json nn load3", creaturesListToAdd.toString())
        creaturesListToAdd.addAll( gson.fromJson<ArrayList<CreaturesData>>(json, type) )
        Log.e("json nn load3", creaturesListToAdd.toString())
    }

}




class CreaturesData(
    var id: Int = 0,
    var pos: Vector2f,
    var velocity: Vector2f,
    var size: Float = 1.0f,
    var genome: Genome,
    var eye: Vector3f,
    var glowing: Boolean = false,
    var speed: Float = 1.0f,
    var wave: Float = 0.0f,
    val generation: Int,
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
    var eyeAngle: Double,
    var breedSize: Float = 1.8f,
    var kidSize: Float = 0.8f,
    var kidsQty:Int = 1,
    var eatMeat:Boolean = false
) : Serializable
