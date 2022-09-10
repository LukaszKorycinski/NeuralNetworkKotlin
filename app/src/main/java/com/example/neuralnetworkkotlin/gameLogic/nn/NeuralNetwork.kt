package com.example.neuralnetworkkotlin.gameLogic.nn

import android.util.Log
import com.google.gson.Gson
import java.io.Serializable

class NeuralNetwork : Serializable {
    fun clone(): NeuralNetwork {
        val stringProject = Gson().toJson(this, NeuralNetwork::class.java)
        return Gson().fromJson<NeuralNetwork>(stringProject, NeuralNetwork::class.java)
    }

    val neuronsPerLayer = 4
    var neurons = arrayOf<Array<Neuron>>()

    fun makeNewBrain(){
        neurons = arrayOf<Array<Neuron>>()

        for (i in 0..3) {
            var inerArray = arrayOf<Neuron>()
            for (j in 0..neuronsPerLayer) {
                val neuron = Neuron()
                neuron.makeRandomWeights(neuronsPerLayer)
                inerArray += neuron
            }
            neurons += inerArray
        }


//        neurons[0][0].weights[0]=1.0f; neurons[0][0].weights[1]=0.0f; neurons[0][0].weights[2]=0.0f; neurons[0][0].weights[3]=0.0f;
//        neurons[1][0].weights[0]=0.0f; neurons[1][0].weights[1]=1.0f; neurons[1][0].weights[2]=0.0f; neurons[1][0].weights[3]=0.0f;
//        neurons[2][0].weights[0]=0.0f; neurons[2][0].weights[1]=0.0f; neurons[2][0].weights[2]=1.0f; neurons[2][0].weights[3]=0.0f;
//        neurons[3][0].weights[0]=0.0f; neurons[3][0].weights[1]=0.0f; neurons[3][0].weights[2]=0.0f; neurons[3][0].weights[3]=1.0f;
//
//        neurons[0][1].weights[0]=1.0f; neurons[0][1].weights[1]=0.0f; neurons[0][1].weights[2]=0.0f; neurons[0][1].weights[3]=0.0f;
//        neurons[1][1].weights[0]=0.0f; neurons[1][1].weights[1]=1.0f; neurons[1][1].weights[2]=0.0f; neurons[1][1].weights[3]=0.0f;
//        neurons[2][1].weights[0]=0.5f; neurons[2][1].weights[1]=1.0f; neurons[2][1].weights[2]=0.5f; neurons[2][1].weights[3]=0.5f;
//        neurons[3][1].weights[0]=0.0f; neurons[3][1].weights[1]=0.0f; neurons[3][1].weights[2]=0.0f; neurons[3][1].weights[3]=1.0f;

    }


    var saved = ArrayList<Float>()


    fun inputToOutput (input :ArrayList<Float>):ArrayList<Float>{

        var change = false

//        if(saved.size != input.size){
//            change = true
//        }else{
//            saved.forEachIndexed { index, fl -> if(input[index]!=fl){ change = true } }
//        }

        if(change) saved = input


        if(change) {
            Log.e("nn","oooooooooooooooooooooooooooooo")
            Log.e("INPUT", input.joinToString { it.toString() })
        }
        val midleInput = ArrayList<Float>()
        for (i in 0..neuronsPerLayer-1){
            val outputTmp = neurons[i][0].inputToOutput(input)
            midleInput.add(outputTmp)
        }// 0 1 0
        if(change)
        Log.e("LAYER1", midleInput.joinToString { it.toString() })

//        val midleInput2 = ArrayList<Float>()
//        for (i in 0..neuronsPerLayer-1){
//            val outputTmp = neurons[i][1].inputToOutput(midleInput)
//            midleInput2.add(outputTmp)
//        }
//        if(change)
//        Log.e("LAYER2", midleInput2.joinToString { it.toString() })
//        val midleInput3 = ArrayList<Float>()
//        for (i in 0..neuronsPerLayer-1){
//            val outputTmp = neurons[i][2].inputToOutput(midleInput2)
//            midleInput3.add(outputTmp)
//        }
//
//        val midleInput4 = ArrayList<Float>()
//        for (i in 0..neuronsPerLayer-1){
//            val outputTmp = neurons[i][3].inputToOutput(midleInput3)
//            midleInput4.add(outputTmp)
//        }
//
//        val finalOutput = ArrayList<Float>()
//        for (i in 0..neuronsPerLayer-1){
//            val outputTmp = neurons[i][4].inputToOutput(midleInput4)
//            finalOutput.add(outputTmp)
//        }

        val finalOutput = ArrayList<Float>()
        for (i in 0..2){
            val outputTmp = neurons[i][1].inputToOutput(midleInput)
            finalOutput.add(outputTmp)
        }
        if(change)
        Log.e("OUT", finalOutput.joinToString { it.toString() })
        return finalOutput
    }


    fun bread(mutantRatio: Int): Int {
        var isMutant = 0
        for (i in 0..2)
            for (j in 0..1) {
                val mutantRandom = (0..mutantRatio).random()
                if(mutantRandom==1){
                    neurons[i][j].muteWeight()
                    isMutant ++
                }

//                val mutantRandomBias = (0..mutantRatio*2).random()
//                if(mutantRandomBias==1){
//                    neurons[i][j].muteBias()
//                    isMutant ++
//                }
            }
        return isMutant
    }


}


