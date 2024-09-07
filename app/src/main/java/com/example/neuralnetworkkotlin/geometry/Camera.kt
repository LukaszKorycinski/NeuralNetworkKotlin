package com.example.neuralnetworkkotlin.geometry

import android.opengl.GLU
import android.opengl.Matrix
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.helpers.getIdentityMatrix
import com.example.neuralnetworkkotlin.helpers.rotateX
import com.example.neuralnetworkkotlin.helpers.translate
import timber.log.Timber
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class Camera {

    val viewProjectionMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    var viewMatrix = FloatArray(16)
    var renderResolution = Vector2f(0f, 0f)

    fun setUpFrame(position: Vector3f, rotation: Vector3f) {
        val tmpMatrix = getIdentityMatrix()
        tmpMatrix.rotateX(rotation.x)
        tmpMatrix.translate(
            position.x,
            position.y,
            position.z,
        )
        viewMatrix = tmpMatrix

        //Timber.e("pos.x = ${position.x}, pos.y = ${position.y}, pos.z = ${position.z}")
        //Timber.e("rot.x = ${rotation.x}, rot.y = ${rotation.y}, rot.z = ${rotation.z}")

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    fun perspectiveINV() {
        val ratio: Float = renderResolution.x / renderResolution.y
        Matrix.perspectiveM(projectionMatrix,0,40.0f, ratio, 3.0f, 150.0f)
    }

    fun unproject(xy: Vector2f): Vector2f {
        val outPoint1: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
        val outPoint2: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        val fixedY = renderResolution.y - xy.y - 1f
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

        val wx = outPoint1[0] / outPoint1[3]
        val wy = outPoint1[1] / outPoint1[3]
        val wz = outPoint1[2] / outPoint1[3]

        val wx2 = outPoint2[0] / outPoint2[3]
        val wy2 = outPoint2[1] / outPoint2[3]
        val wz2 = outPoint2[2] / outPoint2[3]

        val f = wy / (wy2 - wy)
        val x2d = wx - f * (wx2 - wx)
        val z2d = wz - f * (wz2 - wz)

        return Vector2f(x2d, z2d)
    }

}