package com.example.neuralnetworkkotlin.strategygame

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.mytech.a3df
import kotlin.random.Random

class UnitData{
    var animf: Float = 0.0f
    var pos = Vector2f()
}

class Unit {

    var unitsData: ArrayList<UnitData> = arrayListOf()

    init {
        for (i in 0..3)
            for (j in 0..2){
                var unit = UnitData()
                unit.animf = Random.nextFloat()
                unit.pos.x = i*1.5f
                unit.pos.y = j*1.0f
                unitsData.add(unit)
            }
    }
}