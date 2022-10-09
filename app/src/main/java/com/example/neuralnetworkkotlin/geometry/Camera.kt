package com.example.neuralnetworkkotlin.geometry

import android.opengl.Matrix
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector4f

class Camera {

    val viewProjectionMatrix = FloatArray(16)
    var lightMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    var viewMatrix = FloatArray(16)
    var nonCamMatrix = FloatArray(16)
    var nonCamViewProjectionMatrix = FloatArray(16)

    fun calculateLightMatrix(position: Vector4f){
        Matrix.setIdentityM(viewMatrix, 0)

        Matrix.rotateM(
            viewMatrix,
            0,
            position.w,
            1.0f,
            0.0f,
            0.0f
        )
        Matrix.translateM(
            viewMatrix,
            0,
            position.x, position.y, position.z
        )

        Matrix.multiplyMM(lightMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }


    fun setUpFrame(position: Vector4f) {
        Matrix.setIdentityM(viewMatrix, 0)

        Matrix.rotateM(
            viewMatrix,
            0,
            position.w,
            1.0f,
            0.0f,
            0.0f
        )
        Matrix.translateM(
            viewMatrix,
            0,
            position.x, position.y, position.z
        )

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    fun perspectiveINV(aspect: Float) {
        Matrix.perspectiveM(projectionMatrix,0,40.0f, aspect, 3.0f, 150.0f)

//        val angle = 1.3f
//        val near = 3.0f
//        val far = 150.0f
//        val f = Math.tan(0.5 * (Math.PI - angle)).toFloat()
//        val range = near - far
//        projectionMatrix[0] = f / aspect
//        projectionMatrix[1] = 0f
//        projectionMatrix[2] = 0f
//        projectionMatrix[3] = 0f
//        projectionMatrix[5] = f
//        projectionMatrix[4] = 0f
//        projectionMatrix[6] = 0f
//        projectionMatrix[7] = 0f
//        projectionMatrix[8] = 0f
//        projectionMatrix[9] = 0f
//        projectionMatrix[10] = far / range
//        projectionMatrix[11] = -1f
//        projectionMatrix[12] = 0f
//        projectionMatrix[13] = 0f
//        projectionMatrix[14] = near * far / range
//        projectionMatrix[15] = 0f
    }

    fun frustrum(ratio: Float) {
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 140f)
    }
}