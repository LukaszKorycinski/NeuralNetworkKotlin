package com.example.neuralnetworkkotlin.helpers

import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector4f
import kotlin.math.cos
import kotlin.math.sin

class ControlHelper {

    var up = false
    var down = false
    var right = false
    var left = false
    var forward = false
    var backward = false
    var rotateUp = false
    var rotateDown = false


    fun rotateDown(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> rotateDown = true
            MotionEvent.ACTION_UP -> rotateDown = false
        }
    }
    fun rotateUp(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> rotateUp = true
            MotionEvent.ACTION_UP -> rotateUp = false
        }
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



    val position = Vector4f(-5.0999975f, -10.500004f, -9.300002f, 52.79998f)
    var wave = 0.0f


    fun lightPosition(): Vector4f{
        return Vector4f(-2.7999997f+ sin(wave)*3.0f, -8.699996f, -16.100027f+ cos(wave)*3.0f, 39.700006f)
    }

    fun updatePosition(): Vector4f {
//        Log.e("Fsdfz",position.toString())

        wave = wave + 0.02f
        if(wave>180.0f){
            wave=0.0f
        }

        if(rotateDown){
            position.w = position.w + 1.1f
        }
        if(rotateUp){
            position.w = position.w - 1.1f
        }

        if(up){
            position.z = position.z + 0.1f
        }
        if(down){
            position.z = position.z - 0.1f
        }
        if(right){
            position.x = position.x + 0.1f
        }
        if(left){
            position.x = position.x - 0.1f
        }
        if(forward){
            position.y = position.y + 0.1f
        }
        if(backward){
            position.y = position.y - 0.1f
        }

        return position
    }


}