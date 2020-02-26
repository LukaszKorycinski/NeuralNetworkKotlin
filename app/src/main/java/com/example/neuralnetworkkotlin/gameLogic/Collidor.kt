package com.example.neuralnetworkkotlin.gameLogic

import android.util.Log
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f

class Collidor(val terrain: Terrain) {


    fun colision(position: Vector2f): Boolean {
        return terrain.collision(Vector2f(position.x/4, position.y/4))
    }


}