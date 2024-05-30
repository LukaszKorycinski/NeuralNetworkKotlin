package com.example.neuralnetworkkotlin.geometry

import android.opengl.Matrix
import com.example.neuralnetworkkotlin.helpers.rotateX
import com.example.neuralnetworkkotlin.helpers.translate
import timber.log.Timber
import javax.vecmath.Vector3f

class Camera {

    val viewProjectionMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    var viewMatrix = FloatArray(16)
    //var nonCamMatrix = FloatArray(16)
    //var nonCamViewProjectionMatrix = FloatArray(16)

    var camOffset = Vector3f(0f, 0f, 0f)

    fun setUpFrame(position: Vector3f, rotation: Vector3f) {
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -50f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //nonCamMatrix = viewMatrix.clone()

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        viewMatrix.rotateX(rotation.x)
        viewMatrix.translate(
            position.x - camOffset.x,
            position.y - camOffset.y,
            position.z - camOffset.z
        )

        Timber.e("pos.x = ${position.x}, pos.y = ${position.y}, pos.z = ${position.z}")
        Timber.e("rot.x = ${rotation.x}, rot.y = ${rotation.y}, rot.z = ${rotation.z}")

        //Matrix.multiplyMM(nonCamViewProjectionMatrix, 0, projectionMatrix, 0, nonCamMatrix, 0)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    fun frustrum(ratio: Float) {
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 70f)
    }
}