package com.example.neuralnetworkkotlin.ext

import android.opengl.Matrix
import com.example.neuralnetworkkotlin.assimp.AiMatrix4x4
import com.example.neuralnetworkkotlin.helpers.getIdentityMatrix
import glm_.mat4x4.Mat4

operator fun FloatArray.times(matrix: FloatArray):FloatArray{
    val outMatrix = getIdentityMatrix()
    Matrix.multiplyMM(outMatrix, 0, this, 0, matrix, 0)
    return  outMatrix
}

fun FloatArray.translate(x:Float, y: Float, z:Float): FloatArray {
    Matrix.translateM(this, 0, x, y, z)
    return this
}

fun FloatArray.inverse(): FloatArray {
    val matrix = FloatArray(16)
    Matrix.invertM(matrix, 0, this, 0)
    return matrix
}
