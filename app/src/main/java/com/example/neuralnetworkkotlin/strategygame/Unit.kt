package com.example.neuralnetworkkotlin.strategygame

import android.util.Log
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import kotlin.random.Random

class UnitData{
    var animf: Float = 0.0f
    var pos = Vector2f()
    var hasFlag: Boolean = false
}

class Unit {

    var unitsData: ArrayList<UnitData> = arrayListOf()

    val rows = 2
    val columns = 3

    init {
        for (i in 0..columns)
            for (j in 0..rows){
                var unit = UnitData()
                unit.animf = Random.nextFloat()
                unit.pos.x = i*1.5f
                unit.pos.y = j*0.85f
                unitsData.add(unit)
            }

//        for (i in 0..3)
//            for (j in 0..2){
//                var unit = UnitData()
//                unit.animf = Random.nextFloat()
//                unit.pos.x = 2.3f+i*1.5f
//                unit.pos.y = -4.5f+j*0.85f
//                unitsData.add(unit)
//            }

        unitsData.get(2).hasFlag = true
    }

var nand = 0

    fun calculateUnistPos(middle: Vector2f){


        nand++

        unitsData.forEachIndexed { index, uData ->



            val posLocal = Vector2f( (index % columns).toFloat(), (index / rows).toFloat()  )



            val direction = Vector2f(uData.pos.x + posLocal.x - middle.x, uData.pos.y + posLocal.y - middle.y).normalizeOrLow(0.1f)
            val step = direction.normalizeOrLow(0.05f)

//            if(index == 0 && nand%100==1){
//                Log.e("dfs","dfs "+direction.length() )
//                Log.e("dfs","dfs "+Vector2f(uData.pos.x + posLocal.x - middle.x, uData.pos.y + posLocal.y - middle.y).length())
//                Log.e("dfs","dfs " +middle)
//                Log.e("dfs","dfs/////////////////////////////////////////////////////")
//            }

            //if( direction.length() > 0.08f ){
                uData.pos.y = uData.pos.y - step.y
                uData.pos.x = uData.pos.x - step.x

                uData.animf = uData.animf + direction.length()// if(direction.length()>0.025) 0.5f else 0.5f
                if (uData.animf>=4.0f)uData.animf=0.0f
            //}




        }

    }

}