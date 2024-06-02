package com.example.neuralnetworkkotlin.helpers

import android.view.MotionEvent
import javax.vecmath.Vector3f

class ControlHelper {

    companion object{
        var modeSwitcher = false
    }

    var up = false
    var down = false
    var right = false
    var left = false

    var angleMinus = false
    var anglePlus = false

    fun upKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> up = true
            MotionEvent.ACTION_UP -> up = false
        }
    }

    fun downKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> down = true
            MotionEvent.ACTION_UP -> down = false
        }
    }

    fun leftKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> right = true
            MotionEvent.ACTION_UP -> right = false
        }
    }

    fun rightKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> left = true
            MotionEvent.ACTION_UP -> left = false
        }
    }

    fun rotateXPlus(action: MotionEvent) {
        when(action.action){
            MotionEvent.ACTION_DOWN -> anglePlus = true
            MotionEvent.ACTION_UP -> anglePlus = false
        }
    }

    fun rotateXMinus(action: MotionEvent) {
        when(action.action){
            MotionEvent.ACTION_DOWN -> angleMinus = true
            MotionEvent.ACTION_UP -> angleMinus = false
        }
    }

    fun onZoom(zoom: Float){
        zoomTmp = zoom
    }

    fun onZoomEnd(zoom: Float){
        position.z = position.z / zoomTmp
        zoomTmp = 1.0f
    }
    //strategy
    //8.099995, pos.y = -29.300076, pos.z = 32.28673
    //-42.79992, rot.y = 0.0, rot.z = 0.0

    //fight
    // -0.9000051, pos.y = -3.8999968, pos.z = 4.3
    //-37.000008, rot.y = 0.0, rot.z = 0.0

    private val positionStrategy = Vector3f(8f, -29.300076f, 32.28673f)
    private val rotationStrategy = Vector3f(-42.79992f, 0f, 0f)

    private val positionFight = Vector3f(-0f, -3.8999968f, 4.3f)
    private val rotationFight = Vector3f(-37.000008f, 0f, 0f)

    val position = positionStrategy
    val rotation = rotationStrategy
    var zoomTmp = 1.0f

    fun updatePosition(): Vector3f {

        val positionOut = Vector3f()

        if(up){
            position.y = position.y - 0.1f
        }
        if(down){
            position.y = position.y + 0.1f
        }
        if(right){
            position.x = position.x - 0.1f
        }
        if(left){
            position.x = position.x + 0.1f
        }
        if(anglePlus){
            rotation.x = rotation.x + 0.1f
        }
        if(angleMinus){
            rotation.x = rotation.x - 0.1f
        }

        positionOut.x = position.x
        positionOut.y = position.y
        positionOut.z = position.z / zoomTmp

        return positionOut
    }

}