package com.example.neuralnetworkkotlin.strategygame

import android.util.Log
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.ext.middle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import kotlin.random.Random

class UnitData{
    var no: Int = 0
    var animf: Float = 0.0f
    var wave: Float = 0.0f
    var pos = Vector2f()
    var destination: ArrayList<Vector2f> = arrayListOf()
    var tmpDestination: ArrayList<Vector2f> = arrayListOf()
    var mutable = false
}

class Units(val side:Int = 0, pos: Vector2f, val color: Vector3f) {

    var unitsData: ArrayList<UnitData> = arrayListOf()

    var order: UnitOrder = UnitOrder.CALM

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
                unit.pos.x = pos.x + i * 1.5f
                unit.pos.y = pos.y + j * 0.85f
                unitsData.add(unit)
            }
    }

    fun select(middle: Vector2f){
        //if(middle.distance())
    }

    private fun callPosLoc(no: Int): Vector2f{
        return Vector2f( -(no / columns).toFloat()*1.5f + (columns-1)*0.75f ,  -(no % rows).toFloat()*0.85f + (rows-1)*0.425f )
    }

    fun clearDestination(){
        unitsData.middle()?.let {
            unitsData.map {
                it.tmpDestination.clear()
                it.destination.clear()
                it.mutable = false
            }
        }
    }
    fun clearDestination(middle: Vector2f): Boolean{

        unitsData.middle()?.let {
            if( middle.distance(it.pos) < 1.7 ){
                unitsData.map {
                    it.tmpDestination = arrayListOf()
                    val posLocal = callPosLoc(it.no)
                    it.tmpDestination.add(Vector2f ( posLocal.x - middle.x, posLocal.y - middle.y ))
                    it.mutable = true
                }
                return true
            }
        }
        return false
    }

    fun addDestination(middle: Vector2f){
        unitsData
            .filter { it.mutable }
            .map { unit ->
            unit.tmpDestination.lastOrNull()?.let{ lastDest ->
                if( middle.distance(lastDest) > 1.5f){
                    val posLocal = callPosLoc(unit.no)
                    unit.tmpDestination.add(Vector2f ( posLocal.x - middle.x, posLocal.y - middle.y ))
                }
            }
        }
    }

    fun closeDestination(){
        unitsData.map {
            it.destination = it.tmpDestination
            it.mutable=false
        }
        order = UnitOrder.WALK
    }


    fun loop2(enemyUnits: List<Vector2f>){

        //val middle = unitsData.middle()?.pos



        unitsData.forEachIndexed { index, uData ->

            var closestEnemyDistance = 1000.0f
            var closestEnemyPos = Vector2f()
            enemyUnits.forEach {  vector2f ->
                if( vector2f.distance(uData.pos)<closestEnemyDistance ){
                    closestEnemyDistance = vector2f.distance(uData.pos).toFloat()
                    closestEnemyPos = vector2f
                }
            }

            var closestAllyDistance = 1000.0f
            var closestAllyPos = Vector2f()
            unitsData.forEach { ally ->
                if( ally.pos.distance(uData.pos)<closestAllyDistance && ally.no!=uData.no){
                    closestAllyDistance = ally.pos.distance(uData.pos).toFloat()
                    closestAllyPos = ally.pos
                }
            }

            if( closestEnemyDistance<1.7f || uData.wave>0.01f) {
                uData.wave += 0.03f
                if (uData.wave >= 1.0f) uData.wave = 0.0f
            }



            val posLocal = callPosLoc(uData.no)

            var vector = Vector2f(0.0f)



//            if( uData.pos.distance(posLocal) > 0.1f ){
//                val direction = Vector2f(-(uData.pos.x + posLocal.x), (uData.pos.y - posLocal.y)).normalizeOrLow(0.1f)
//                vector = Vector2f.sum(vector, direction.normalizeOrLow(0.04f))
//            }


            if(uData.destination.isNotEmpty() && order == UnitOrder.WALK){
                val direction = Vector2f(uData.pos.x + uData.destination.get(0).x, uData.pos.y + uData.destination.get(0).y).normalizeOrLow(0.1f)
                vector = Vector2f.sum(vector, direction.normalizeOrLow(0.08f))

                if(direction.length()<0.01f){
                    uData.destination.removeAt(0)
                    if(uData.destination.size == 0 && closestEnemyDistance>1.7f){
                        order = UnitOrder.CALM
                    }
                }
            }


//            if( closestEnemyDistance > 5.0f && order == UnitOrder.FIGHT){
//                order = UnitOrder.CALM
//            }

            if( (closestEnemyDistance < 2.5f && closestEnemyDistance > 1.0f)){
                if (order == UnitOrder.FIGHT){
                    val direction = Vector2f(uData.pos.x - closestEnemyPos.x, uData.pos.y - closestEnemyPos.y).normalizeOrLow(0.2f)
                    vector = Vector2f.sum(vector, direction.normalizeOrLow(0.03f))
                }

                if(order != UnitOrder.DISENGAGE){
                    order = UnitOrder.FIGHT
                    clearDestination()
                }
            }

            vector = vector.normalizeOrLow(0.05f)


            if( closestEnemyDistance < 0.7f || closestAllyDistance < 0.5f ){
                val direction = if(closestAllyDistance < closestEnemyDistance){
                    Vector2f(-(uData.pos.x + closestAllyPos.x), -(uData.pos.y - closestAllyPos.y)).normalizeOrLow(0.1f)
                }else{
                    Vector2f(-(uData.pos.x + closestEnemyPos.x), -(uData.pos.y - closestEnemyPos.y)).normalizeOrLow(0.1f)
                }
                vector = Vector2f.sum(vector, direction.normalizeOrLow(0.1f))
            }



//            var newPos = Vector2f(uData.pos.x - vector.x, uData.pos.y - vector.y)
//
//            if( (newPos.distance(closestAllyPos) < closestAllyDistance || newPos.distance(closestEnemyPos) < closestEnemyDistance)
//                && newPos.distance(closestEnemyPos) < 0.7f && newPos.distance(closestAllyPos) < 0.5f){
//
//            }else{
//                uData.pos = newPos
//            }



            if(vector.length()>0.01f){
                uData.animf = uData.animf + 0.05f
                if (uData.animf>=4.0f)uData.animf=0.0f
            }


        }
    }



    fun loop(enemyUnits: List<Vector2f>){
        unitsData.forEachIndexed { index, uData ->

            var closestEnemyDistance = 1000.0f
            var closestEnemyPos = Vector2f()
            enemyUnits.forEachIndexed { index, vector2f ->
                if( vector2f.distance(uData.pos)<closestEnemyDistance ){
                    closestEnemyDistance = vector2f.distance(uData.pos).toFloat()
                    closestEnemyPos = vector2f
                }
            }

            var clossestAllyDistance = 1000.0f
            var closestAllyPos = Vector2f()
            unitsData.forEachIndexed { index, ally ->
                if( ally.pos.distance(uData.pos)<clossestAllyDistance && ally.no!=uData.no){
                    clossestAllyDistance = ally.pos.distance(uData.pos).toFloat()
                    closestAllyPos = ally.pos
                }
            }

            if( closestEnemyDistance<1.7f || uData.wave>0.01f) {
                uData.wave += 0.03f
                if (uData.wave >= 1.0f) uData.wave = 0.0f
            }

            if(uData.destination.isNotEmpty()){
                val direction = Vector2f(uData.pos.x + uData.destination.get(0).x, uData.pos.y + uData.destination.get(0).y).normalizeOrLow(0.1f)

                val step = direction.normalizeOrLow(0.05f)

                uData.pos.y = uData.pos.y - step.y
                uData.pos.x = uData.pos.x - step.x

                if(direction.length()<0.01f){
                    uData.destination.removeAt(0)
                }

                uData.animf = uData.animf + 0.05f
                if (uData.animf>=4.0f)uData.animf=0.0f
            }else
            if( closestEnemyDistance < 0.7f || clossestAllyDistance < 0.5f ){
                val direction = if(clossestAllyDistance < closestEnemyDistance){
                    Vector2f((uData.pos.x + closestAllyPos.x), (uData.pos.y - closestAllyPos.y)).normalizeOrLow(0.1f)
                }else{
                    Vector2f((uData.pos.x + closestEnemyPos.x), (uData.pos.y - closestEnemyPos.y)).normalizeOrLow(0.1f)
                }

                val step = direction.normalizeOrLow(0.05f)

                uData.pos.y = uData.pos.y + step.y
                uData.pos.x = uData.pos.x + step.x

                uData.animf = uData.animf + 0.05f
                if (uData.animf>=4.0f)uData.animf=0.0f
            } else             if( closestEnemyDistance < 1.7f){
                val direction = Vector2f(uData.pos.x + closestEnemyPos.x, uData.pos.y - closestEnemyPos.y).normalizeOrLow(0.1f)

                val step = direction.normalizeOrLow(0.05f)

                uData.pos.y = uData.pos.y - step.y
                uData.pos.x = uData.pos.x - step.x

                uData.animf = uData.animf + 0.05f
                if (uData.animf>=4.0f)uData.animf=0.0f
            }

        }
    }
}

enum class UnitOrder {
    CALM, WALK, FIGHT, DISENGAGE
}



