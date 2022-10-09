package com.example.neuralnetworkkotlin.strategygame

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.mytech.a3df
import com.example.neuralnetworkkotlin.strategygame.Unit

class Banner {

    var units = Unit()

    fun draw(texture: Int, shaderProgram: Int, A3df: a3df, camera: Camera){
        GLES20.glUseProgram(shaderProgram)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)


        for (uData in units.unitsData){
            uData.animf = uData.animf + 0.1f
            if (uData.animf>=4.0f)uData.animf=0.0f

            val tmpMatrix = FloatArray(16)

            val iVPMatrix = GLES20.glGetUniformLocation(shaderProgram, "u_VPMatrix") //, iVMatrix;

            Matrix.setIdentityM(tmpMatrix, 0)

            Matrix.translateM(tmpMatrix, 0, uData.pos.x, 0.0f, uData.pos.y)

            Matrix.multiplyMM(tmpMatrix, 0, camera.viewProjectionMatrix, 0, tmpMatrix, 0)
            GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

            A3df.DrawAnimModel(0, texture, shaderProgram, uData.animf)
        }
    }
}