package com.example.neuralnetworkkotlin.gameLogic.nn

import java.lang.Math.pow
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random


//dodać funkcję
//dodać biasy do neuronów


class Neuron{
    var weights = ArrayList<Float>()
    //var bias = 1.0f

    fun inputToOutput (input :ArrayList<Float>):Float{
        var sum = 0.0f



        for(i in 0..input.size-1){
//            when(i){
//                0 -> {sum = sum +  input.get(i) * weights.get(i)}
//                1 -> {sum = sum +  input.get(i) * weights.get(i)}
//                2 -> {sum = sum +  input.get(i) * weights.get(i)}
//                3 -> {sum = sum +  input.get(i) * weights.get(i)}
//            }
            sum = sum +  input.get(i) * weights.get(i)
        }

        return sum//sigmoid( sum )
    }

    private fun sigmoid(x: Float): Float {
        return (1f/( 1f + pow(Math.E,(-1*x.toDouble())))).toFloat()
    }

    //fun muteBias() {
        //bias = bias + Random.nextFloat() * 0.5f - 0.25f
    //}

    fun muteWeight() {
//        val mutantRandom = (0..weights.size-1).random()
//        weights.set(mutantRandom, generateRandomDouble(-1.5f, 1.5f))
        val mutantRandom = (0..weights.size-1).random()
        val currentWeight = weights.get(mutantRandom)
        weights.set(mutantRandom, currentWeight+generateRandomDouble(-0.1f, 0.1f))
    }

    fun makeRandomWeights(qty:Int){
        weights.clear()
        for(i in 0..qty-1){
            weights.add( generateRandomDouble(-1.5f, 1.5f) )
        }
        ////////bias = Random.nextFloat() * 2.0f - 1.0f
    }

    fun generateRandomDouble(min: Float, max: Float): Float {
        return Random.nextFloat()*(max*2)-min
    }


}