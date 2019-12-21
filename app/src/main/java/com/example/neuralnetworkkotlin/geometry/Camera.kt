package com.example.neuralnetworkkotlin.geometry

import android.opengl.Matrix

class Camera {

     val viewProjectionMatrix = FloatArray(16)
     val projectionMatrix = FloatArray(16)
     var viewMatrix = FloatArray(16)

    var position = Vector3f(0.0f, 0.0f, 0.0f)


    fun setUpFrame() {
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -50f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.translateM(viewMatrix, 0, position.x, position.y, position.z)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    fun frustrum(ratio: Float) {
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 70f)
    }
}