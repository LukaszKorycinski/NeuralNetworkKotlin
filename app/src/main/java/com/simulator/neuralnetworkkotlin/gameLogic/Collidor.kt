package com.simulator.neuralnetworkkotlin.gameLogic

import com.simulator.neuralnetworkkotlin.geometry.Terrain
import com.simulator.neuralnetworkkotlin.geometry.collada.converter.Vector2f

class Collidor(val terrain: Terrain) {


    fun colision(position: Vector2f): Boolean {
        return terrain.collision(Vector2f(position.x/4, position.y/4))
    }


}