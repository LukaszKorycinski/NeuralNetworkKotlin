package com.example.neuralnetworkkotlin.strategygame

import android.util.Log
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import kotlin.random.Random

class UnitData{
    var no: Int = 0
    var animf: Float = 0.0f
    var pos = Vector2f()
    var destination: ArrayList<Vector2f> = arrayListOf()
}

class Units {

    var unitsData: ArrayList<UnitData> = arrayListOf()
    var isSelected = true


    val columns = 3
    val rows = 3

    init {
        var index = 0
        for (i in 0..columns-1)
            for (j in 0..rows-1){
                var unit = UnitData()
                unit.no = index
                index++

                unit.animf = Random.nextFloat()
                unit.pos.x = i*1.5f
                unit.pos.y = j*0.85f
                unitsData.add(unit)
            }
    }

    fun select(middle: Vector2f){
        //if(middle.distance())
    }

    private fun callPosLoc(no: Int): Vector2f{
        return Vector2f( -(no / columns).toFloat()*1.5f + (columns-1)*0.75f ,  -(no % rows).toFloat()*0.85f + (rows-1)*0.425f )
    }

    fun clearDestination(middle: Vector2f){
        unitsData.map {
            it.destination = arrayListOf()
//            it.destination.add(middle)
            val posLocal = callPosLoc(it.no)
            it.destination.add(Vector2f ( posLocal.x - middle.x, posLocal.y - middle.y ))
            Log.e("add","dst: "+it.no+" "+Vector2f ( posLocal.x, posLocal.y ))
        }
    }

    fun addDestination(middle: Vector2f){
        unitsData.map { unit ->
            unit.destination.lastOrNull()?.let{ lastDest ->
                if( middle.distance(lastDest) > 1.5f){
                    val posLocal = callPosLoc(unit.no)
                    unit.destination.add(Vector2f ( posLocal.x - middle.x, posLocal.y - middle.y ))
//                    unit.destination.add(middle)
                }
            }
        }
    }

    fun loop(){
        unitsData.forEachIndexed { index, uData ->

            if(uData.destination.isNotEmpty()){
                val direction = Vector2f(uData.pos.x + uData.destination.get(0).x, uData.pos.y + uData.destination.get(0).y).normalizeOrLow(0.1f)

                val step = direction.normalizeOrLow(0.05f)


                uData.pos.y = uData.pos.y - step.y
                uData.pos.x = uData.pos.x - step.x

                if(direction.length()<0.01f){
                    uData.destination.removeAt(0)
                }
                uData.animf = uData.animf + direction.length()


                if (uData.animf>=4.0f)uData.animf=0.0f
            }
        }
    }
}