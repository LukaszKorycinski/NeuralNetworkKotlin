package com.example.neuralnetworkkotlin.helpers

import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.vectors.PositionRotation
import javax.vecmath.Vector3f

class ControlHelper {

    companion object{
        var modeSwitcher = false
    }

    var up = false
    var down = false
    var right = false
    var left = false
    var upZ = false
    var downZ = false

    var angleMinus = false
    var anglePlus = false

    fun zDownKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> upZ = true
            MotionEvent.ACTION_UP -> upZ = false
        }
    }

    fun zUpKey(action: MotionEvent){
        when(action.action){
            MotionEvent.ACTION_DOWN -> downZ = true
            MotionEvent.ACTION_UP -> downZ = false
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

    fun onZoom(zoom: Float){}

    fun onZoomEnd(zoom: Float){}

    private val strategy = PositionRotation(Vector3f(  0f, -15.600023f,  -10.290006f), Vector3f(57.399696f, 0f, 0f))
    private val fight = PositionRotation(Vector3f(-0f, -3.8999968f, 4.3f), Vector3f(-37.000008f, 0f, 0f))
    private val debug = PositionRotation(Vector3f(-0.6f, -4.9999976f, -5.39f), Vector3f(42.299927f, 0f, 0f))
    private val zoom = PositionRotation(Vector3f(0.0f, -4.499998f, -3.3900018f), Vector3f(57.399696f, 0.0f, 0.0f))

    val positionRotation = strategy

    fun updatePosition(): Vector3f {

        val positionOut = Vector3f()

        if(up){
            positionRotation.position.y -= 0.1f
        }
        if(down){
            positionRotation.position.y += 0.1f
        }
        if(upZ){
            positionRotation.position.z -= 0.1f
        }
        if(downZ){
            positionRotation.position.z += 0.1f
        }
        if(right){
            positionRotation.position.x += 0.1f
        }
        if(left){
            positionRotation.position.x -= 0.1f
        }
        if(anglePlus){
            positionRotation.rotation.x += 0.1f
        }
        if(angleMinus){
            positionRotation.rotation.x -= 0.1f
        }

        positionOut.x = positionRotation.position.x
        positionOut.y = positionRotation.position.y
        positionOut.z = positionRotation.position.z

        return positionOut
    }

}