package com.example.neuralnetworkkotlin.repo

import com.example.neuralnetworkkotlin.ext.middle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.strategygame.Units

class BannersRepo {
    var units: ArrayList<Units> = arrayListOf()

    fun clearDestination(middle: Vector2f): Boolean {
        return units.minByOrNull { it.unitsData.middle()?.pos?.distance(middle) ?: 1000.0 }?.clearDestination(middle) == true
    }

    fun addDestination(middle: Vector2f) {
        units.forEach { it.addDestination(middle) }
    }

    fun closeDestination() {
        units.forEach { it.closeDestination() }
    }

    fun loop() {
        units.forEach { it.loop2(getEnemyPos(it.side)) }
    }

    fun getEnemyPos(side: Int): List<Vector2f> {
        return units.filter { it.side != side }.flatMap { it.unitsData.map { it.pos } }
    }

    init {
        units += Units(0, Vector2f(-4f, 2f), Vector3f(0.8f,0.1f,0.1f))
        units += Units(1,  Vector2f(5f, 3f), Vector3f(0.1f,0.1f,0.8f))
    }
}