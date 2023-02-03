package com.example.neuralnetworkkotlin.strategygame

import android.opengl.GLES20
import android.opengl.Matrix
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Matrices
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.mytech.a3df

class Banners {


    var units = Units()
    var pointer = Pointer()

    fun onClick(motionEvent: MotionEvent, pos: Vector2f, matrices: Matrices) {//musi przekazać xy żeby zaznaczyć,

        val pointer3d = matrices.unproject(pos)

        when (motionEvent.action){
            MotionEvent.ACTION_DOWN -> {
                units.clearDestination(pointer3d)
                pointer.clearDestination(pointer3d, units.unitsData.get(units.unitsData.size/2).pos)
                pointer.recalculate()
            }
            MotionEvent.ACTION_UP -> {
                units.addDestination(pointer3d)
                units.closeDestination()
                pointer.addDestination(pointer3d)
                pointer.end()
                pointer.recalculate()
            }
            MotionEvent.ACTION_MOVE -> {
                units.addDestination(pointer3d)
                pointer.addDestination(pointer3d)
                pointer.recalculate()
            }
            else -> {}
        }
    }


    fun logic(){
        units.loop()
    }


    fun draw(texture: Int,lightMatrix: FloatArray?,  shadowMapHandle: Int?, shaderProgram: Int, A3df: a3df, matrices: Matrices){//gpu instancing trzeba zrobić tu i w trawie
        GLES20.glUseProgram(shaderProgram)

        val texLoc = GLES20.glGetUniformLocation(shaderProgram, "u_Texture")
        GLES20.glUniform1i(texLoc, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)

        shadowMapHandle?.let{
            val shadowmapHandler = GLES20.glGetUniformLocation(shaderProgram, "u_ShadowMap")
            GLES20.glUniform1i(shadowmapHandler, 1)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMapHandle)

            val lightMatrixHandler = GLES20.glGetUniformLocation(shaderProgram, "lightMatrix")
            GLES20.glUniformMatrix4fv(lightMatrixHandler, 1, false, matrices.lightMatrix, 0)
        }

        for (uData in units.unitsData){
            val tmpMatrix = FloatArray(16)
            Matrix.setIdentityM(tmpMatrix, 0)
            Matrix.translateM(tmpMatrix, 0, uData.pos.x,  0.0f, uData.pos.y )
            val scale = if(uData.mutable) 1.2f else 1.0f
            Matrix.scaleM(tmpMatrix, 0, scale, scale, scale)

            val iVPMatrix = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix") //, iVMatrix;
            Matrix.multiplyMM(tmpMatrix, 0, matrices.viewProjectionMatrix, 0, tmpMatrix, 0)
            GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

            lightMatrix?.let {
                val lightMatrixLoc = GLES20.glGetUniformLocation(shaderProgram, "lightMatrix") //, iVMatrix;
                GLES20.glUniformMatrix4fv(lightMatrixLoc, 1, false, it, 0)
            }


            A3df.DrawAnimModel(0, shaderProgram, uData.animf)
        }
    }
}