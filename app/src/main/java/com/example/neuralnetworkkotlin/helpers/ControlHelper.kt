package com.example.neuralnetworkkotlin.helpers

import android.util.Log
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f

class ControlHelper {

    var up = false
    var down = false
    var right = false
    var left = false
    var forward = false
    var backward = false

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

    fun qKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> forward = true
            MotionEvent.ACTION_UP -> forward = false
        }
    }

    fun eKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> backward = true
            MotionEvent.ACTION_UP -> backward = false
        }
    }

    val position = Vector3f(0f, 0f, -5.0f)

    fun updatePosition(): Vector3f {
        //Log.e("position", ""+position.toString())

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
        if(forward){
            position.z = position.z - 0.1f
        }
        if(backward){
            position.z = position.z + 0.1f
        }
        return position
    }
}