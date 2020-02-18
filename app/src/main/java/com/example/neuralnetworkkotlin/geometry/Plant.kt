package com.example.neuralnetworkkotlin.geometry

import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f


class Plant {
    val plantsList = ArrayList<PlantsData>()



    fun loop(){
        plantsList.forEach {

            it.size =  Math.min(it.size+0.001f, 0.5f)
        }
    }

}

class PlantsData (
    val pos:Vector2f,
    var size:Float = 0.0f
){

}