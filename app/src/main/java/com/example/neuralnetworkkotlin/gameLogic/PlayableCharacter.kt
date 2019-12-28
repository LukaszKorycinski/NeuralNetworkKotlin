package com.example.neuralnetworkkotlin.gameLogic

import com.example.neuralnetworkkotlin.geometry.DrawModel
import com.example.neuralnetworkkotlin.geometry.Vector3f

class PlayableCharacter () {

    var drawModel = DrawModel()
    var position = Vector3f()





    fun draw(){
        drawModel.draw()
    }



}