package com.example.neuralnetworkkotlin.helpers

import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Vector3f

class ControlHelper {

    var up = false
    var down = false
    var right = false
    var left = false


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

    val position = Vector3f()

    fun updatePosition(): Vector3f {
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
        return position
    }
}