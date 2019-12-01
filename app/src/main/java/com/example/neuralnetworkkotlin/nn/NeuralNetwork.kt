package com.example.neuralnetworkkotlin.nn

import android.util.Log
import com.google.gson.Gson
import kotlin.random.Random

class NeuralNetwork{
    fun clone(): NeuralNetwork {
        val stringProject = Gson().toJson(this, NeuralNetwork::class.java)
        return Gson().fromJson<NeuralNetwork>(stringProject, NeuralNetwork::class.java)
    }

    var neurons = arrayOf<Array<Neuron>>()

    fun makeNewBrain(){
        for (i in 0..4) {
            var inerArray = arrayOf<Neuron>()
            for (j in 0..4) {
                var neuron = Neuron()
                neuron.makeRandomWeights(5)
                inerArray += neuron
            }
            neurons += inerArray
        }
    }


    fun inputToOutput (input :ArrayList<Float>):ArrayList<Float>{
        val midleInput = ArrayList<Float>()
        for (i in 0..4){
            val outputTmp = neurons[i][0].inputToOutput(input)
            midleInput.add(outputTmp)
        }

        val midleInput2 = ArrayList<Float>()
        for (i in 0..4){
            val outputTmp = neurons[i][1].inputToOutput(midleInput)
            midleInput2.add(outputTmp)
        }

        val midleInput3 = ArrayList<Float>()
        for (i in 0..4){
            val outputTmp = neurons[i][2].inputToOutput(midleInput2)
            midleInput3.add(outputTmp)
        }

        val midleInput4 = ArrayList<Float>()
        for (i in 0..4){
            val outputTmp = neurons[i][3].inputToOutput(midleInput3)
            midleInput4.add(outputTmp)
        }

        val finalOutput = ArrayList<Float>()
        for (i in 0..1){
            val outputTmp = neurons[i][4].inputToOutput(midleInput4)
            finalOutput.add(outputTmp)
        }


        return finalOutput
    }


    fun bread() {
        for (i in 0..4)
            for (j in 0..4) {
                //val parentGeneRandom = (0..1).random()
                val mutantRandom = (0..20).random()

//                if(parentGeneRandom==1) {
//                    neurons[i][j] = fastCar.neurons[i][j]
//                }
                if(mutantRandom==1){
                    neurons[i][j].muteWeight()
                }

            }


    }


}


