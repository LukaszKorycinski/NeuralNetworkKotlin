package com.example.neuralnetworkkotlin.strategygame

import android.opengl.GLES20
import android.opengl.Matrix
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Matrices
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.mytech.a3df
import com.example.neuralnetworkkotlin.repo.BannersRepo
import org.koin.java.KoinJavaComponent.inject

class Banners (){

    val bannersRepo by inject<BannersRepo>(BannersRepo::class.java)

    var pointer = Pointer()

    fun onClick(motionEvent: MotionEvent, pos: Vector2f, matrices: Matrices) {//musi przekazać xy żeby zaznaczyć,

        val pointer3d = matrices.unproject(pos)

        when (motionEvent.action){
            MotionEvent.ACTION_DOWN -> {
                bannersRepo.clearDestination(pointer3d)
                pointer.clearDestination(pointer3d)
            }
            MotionEvent.ACTION_MOVE -> {
                bannersRepo.addDestination(pointer3d)
                pointer.addDestination(pointer3d)
            }
            MotionEvent.ACTION_UP -> {
                bannersRepo.addDestination(pointer3d)
                bannersRepo.closeDestination()
                pointer.addDestination(pointer3d)
            }
            else -> {}
        }
    }


    fun logic(){
        bannersRepo.loop()
    }


    fun draw(texture: IntArray,lightMatrix: FloatArray?,  shadowMapHandle: Int?, shaderProgram: Int, A3df: a3df, matrices: Matrices){//gpu instancing trzeba zrobić tu i w trawie
        GLES20.glUseProgram(shaderProgram)

        val texLoc = GLES20.glGetUniformLocation(shaderProgram, "u_Texture")
        GLES20.glUniform1i(texLoc, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])

        shadowMapHandle?.let{
            val shadowmapHandler = GLES20.glGetUniformLocation(shaderProgram, "u_ShadowMap")
            GLES20.glUniform1i(shadowmapHandler, 1)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMapHandle)

            val lightMatrixHandler = GLES20.glGetUniformLocation(shaderProgram, "lightMatrix")
            GLES20.glUniformMatrix4fv(lightMatrixHandler, 1, false, matrices.lightMatrix, 0)
        }

        val texColorsLoc = GLES20.glGetUniformLocation(shaderProgram, "uColors_Texture")
        GLES20.glUniform1i(texColorsLoc, 2)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[5])

        for (unitsList in bannersRepo.units) {
            val propertyHandler = GLES20.glGetUniformLocation(shaderProgram, "unitColor")
            GLES20.glUniform3f(propertyHandler, unitsList.color.x, unitsList.color.y, unitsList.color.z)

            for (uData in unitsList.unitsData) {
                val tmpMatrix = FloatArray(16)
                Matrix.setIdentityM(tmpMatrix, 0)
                Matrix.translateM(tmpMatrix, 0, uData.pos.x, 0.0f, uData.pos.y)
                val scale = if (uData.mutable) 1.2f else 1.0f
                Matrix.scaleM(tmpMatrix, 0, scale, scale, scale)

                val iVPMatrix =
                    GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix") //, iVMatrix;
                Matrix.multiplyMM(tmpMatrix, 0, matrices.viewProjectionMatrix, 0, tmpMatrix, 0)
                GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

                lightMatrix?.let {
                    val lightMatrixLoc =
                        GLES20.glGetUniformLocation(shaderProgram, "lightMatrix") //, iVMatrix;
                    GLES20.glUniformMatrix4fv(lightMatrixLoc, 1, false, it, 0)
                }


                A3df.DrawAnimModel(0, shaderProgram, uData.animf, uData.wave)
            }
        }
    }
}