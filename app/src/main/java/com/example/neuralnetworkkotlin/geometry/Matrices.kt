package com.example.neuralnetworkkotlin.geometry

import android.opengl.GLU
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector4f
import kotlin.random.Random

class Matrices {

    val viewProjectionMatrix = FloatArray(16)
    var lightMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    var viewMatrix = FloatArray(16)
    var nonCamMatrix = FloatArray(16)
    var nonCamViewProjectionMatrix = FloatArray(16)

    fun calculateLightMatrix(position: Vector4f){
        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)

        Matrix.rotateM(
            tmpMatrix,
            0,
            position.w,
            1.0f,
            0.0f,
            0.0f
        )
        Matrix.translateM(
            tmpMatrix,
            0,
            position.x, position.y, position.z
        )

        Matrix.multiplyMM(lightMatrix, 0, projectionMatrix, 0, tmpMatrix, 0)
    }


    var renderResolution = Vector2f()

    fun perspectiveINV() {
        val ratio: Float = renderResolution.x / renderResolution.y
        Matrix.perspectiveM(projectionMatrix,0,40.0f, ratio, 3.0f, 150.0f)
    }







    fun setUpFrame(position: Vector4f, shadowPass: Boolean = false) {
        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)

        Matrix.rotateM(
            tmpMatrix,
            0,
            position.w,
            1.0f,
            0.0f,
            0.0f
        )
        Matrix.translateM(
            tmpMatrix,
            0,
            position.x, position.y, position.z
        )

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, tmpMatrix, 0)

        if(!shadowPass){
            viewMatrix = tmpMatrix
        }
    }

    fun unproject(xy: Vector2f): Vector2f {
        val outPoint1: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
        val outPoint2: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        val fixedY = renderResolution.y - xy.y  - 1f
        GLU.gluUnProject(
            xy.x,fixedY,0.0f,
            viewMatrix, 0,
            projectionMatrix, 0,
            intArrayOf(0, 0, renderResolution.x.toInt(), renderResolution.y.toInt()), 0,
            outPoint1, 0
        )

        GLU.gluUnProject(
            xy.x,fixedY,1.0f,
            viewMatrix, 0,
            projectionMatrix, 0,
            intArrayOf(0, 0, renderResolution.x.toInt(), renderResolution.y.toInt()), 0,
            outPoint2, 0
        )

        val wx = outPoint1.get(0) / outPoint1.get(3)
        val wy = outPoint1.get(1) / outPoint1.get(3)
        val wz = outPoint1.get(2) / outPoint1.get(3)

        val wx2 = outPoint2.get(0) / outPoint2.get(3)
        val wy2 = outPoint2.get(1) / outPoint2.get(3)
        val wz2 = outPoint2.get(2) / outPoint2.get(3)

        val f = wy / (wy2 - wy)
        val x2d = wx - f * (wx2 - wx)
        val z2d = wz - f * (wz2 - wz)

        return Vector2f(x2d, z2d)
    }

}