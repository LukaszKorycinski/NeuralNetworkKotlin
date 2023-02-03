package com.example.neuralnetworkkotlin.geometry

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.mytech.f3ds
import com.example.neuralnetworkkotlin.renderer.TexturesLoader

class StaticMesh {
    fun draw(matrices: Matrices, textures: TexturesLoader, files3ds: f3ds, lightMatrix: FloatArray?, shadowMapHandle: Int?, shaderProgram: Int){//gpu instancing trzeba zrobić tu i w trawie
        GLES20.glUseProgram(shaderProgram)

        val texLoc = GLES20.glGetUniformLocation(shaderProgram, "u_Texture")
        GLES20.glUniform1i(texLoc, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[4])

        shadowMapHandle?.let{
            val shadowmapHandler = GLES20.glGetUniformLocation(shaderProgram, "u_ShadowMap")
            GLES20.glUniform1i(shadowmapHandler, 1)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMapHandle)

            val lightMatrixHandler = GLES20.glGetUniformLocation(shaderProgram, "lightMatrix")
            GLES20.glUniformMatrix4fv(lightMatrixHandler, 1, false, matrices.lightMatrix, 0)
        }

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, 0.0f,  0.0f, 0.0f )
        val scale = 0.8f
        Matrix.scaleM(tmpMatrix, 0, scale, scale, scale)

        val iVPMatrix = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix") //, iVMatrix;
        Matrix.multiplyMM(tmpMatrix, 0, matrices.viewProjectionMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        lightMatrix?.let {
                val lightMatrixLoc = GLES20.glGetUniformLocation(shaderProgram, "lightMatrix") //, iVMatrix;
                GLES20.glUniformMatrix4fv(lightMatrixLoc, 1, false, it, 0) }

        files3ds.DrawModel(1, shaderProgram)
    }

}