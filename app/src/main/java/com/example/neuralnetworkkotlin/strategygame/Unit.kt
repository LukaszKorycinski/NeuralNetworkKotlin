package com.example.neuralnetworkkotlin.strategygame

import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import kotlin.random.Random

class UnitData{
    var animf: Float = 0.0f
    var pos = Vector2f()
    var hasFlag: Boolean = false
}

class Unit {

    var unitsData: ArrayList<UnitData> = arrayListOf()

    init {
        for (i in 0..3)
            for (j in 0..2){
                var unit = UnitData()
                unit.animf = Random.nextFloat()
                unit.pos.x = i*1.5f
                unit.pos.y = j*0.85f
                unitsData.add(unit)
            }

        for (i in 0..3)
            for (j in 0..2){
                var unit = UnitData()
                unit.animf = Random.nextFloat()
                unit.pos.x = 2.3f+i*1.5f
                unit.pos.y = -4.5f+j*0.85f
                unitsData.add(unit)
            }

        unitsData.get(2).hasFlag = true
    }


}