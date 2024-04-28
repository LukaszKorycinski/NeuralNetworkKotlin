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
    val matrix = this
    Matrix.translateM(matrix, 0, x, y, z)
    return matrix
}

fun AiMatrix4x4.flip(): FloatArray {
    val matrix = AiMatrix4x4()

    for (y in 0..3) {
        for (x in 0..3) {
            matrix[x][y] = this[y][x]
        }
    }
    return matrix.toFloatArray()
}