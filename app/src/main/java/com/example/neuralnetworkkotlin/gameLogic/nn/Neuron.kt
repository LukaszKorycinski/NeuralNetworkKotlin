package com.example.neuralnetworkkotlin.gameLogic.nn

import java.util.concurrent.ThreadLocalRandom

class Neuron{
    var weights = ArrayList<Float>()

    fun inputToOutput (input :ArrayList<Float>):Float{
        var sum = 0.0f

        for(i in 0..input.size-1){
            var input = input.get(i)
            var waight = weights.get(i)
            sum = sum +  input * waight
        }

        var output:Float = sum
        return output
    }

    fun muteWeight() {
//        val mutantRandom = (0..weights.size-1).random()
//        weights.set(mutantRandom, generateRandomDouble(-1.5f, 1.5f))
        val mutantRandom = (0..weights.size-1).random()
        val currentWeight = weights.get(mutantRandom)
        weights.set(mutantRandom, currentWeight+generateRandomDouble(-0.5f, 0.5f))
    }

    fun makeRandomWeights(qty:Int){
        weights.clear()
        for(i in 0..qty-1){
            weights.add( generateRandomDouble(-1.5f, 1.5f) )
        }
    }

    fun generateRandomDouble(min: Float, max: Float): Float {
        return ThreadLocalRandom.current().nextFloat()*3.0f-1.5f
    }


}