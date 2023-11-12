package com.example.neuralnetworkkotlin.helpers

import android.util.Log
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f

class ControlHelper {

    var up = false
    var down = false
    var right = false
    var left = false
    var mode = true

    fun switchMode(isChecked: Boolean) {
        this.mode = isChecked
    }

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

    fun onZoom(zoom: Float){
        zoomTmp = zoom
    }

    fun onZoomEnd(zoom: Float){
        position.z = position.z / zoomTmp
        zoomTmp = 1.0f
    }




    val position = Vector3f(0f, 0f, 15.0f)
    var zoomTmp = 0.0f

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

        positionOut.x = position.x
        positionOut.y = position.y
        positionOut.z = position.z / zoomTmp

        return positionOut
    }

    fun updateControls(): Vector3f {

        val positionOut = Vector3f()

        if(up){
            positionOut.y = 0.1f
        }
        if(down){
            positionOut.y = -0.1f
        }
        if(right){
            positionOut.x = 0.1f
        }
        if(left){
            positionOut.x = -0.1f
        }



        return positionOut
    }
}