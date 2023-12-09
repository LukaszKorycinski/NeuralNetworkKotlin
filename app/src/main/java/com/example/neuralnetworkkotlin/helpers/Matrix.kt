package com.example.neuralnetworkkotlin.helpers

import android.opengl.Matrix

fun mullEvery(framesMatrices: FloatArray, inv_bind_matrix: FloatArray): FloatArray {//matrixes każdej klatki danej kościi z pliku     matrix current bone INV_BIND_MATRIX
    val matricesQty = framesMatrices.size / 16
    val matrixesOut = framesMatrices

    for (i in 0..matricesQty - 1) {
        val tmpMatrix = FloatArray(16)

        for (j in 0..15) {
            tmpMatrix[j] = framesMatrices[16 * i + j]//macierze zapisane po kolei
        }

        //Matrix.invertM(matrix, 0, matrix, 0)
        Matrix.multiplyMM(tmpMatrix, 0, inv_bind_matrix , 0, tmpMatrix, 0)

        for (j in 0..15) {
            matrixesOut[16 * i +j] = tmpMatrix[j]
        }
    }

    return matrixesOut
}