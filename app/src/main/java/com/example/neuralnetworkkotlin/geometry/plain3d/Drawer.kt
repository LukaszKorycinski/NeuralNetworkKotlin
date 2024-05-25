package com.example.neuralnetworkkotlin.geometry.plain3d

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.ext.Vector2f
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f

class Drawer(val textures: TexturesLoader) {

    fun draw(mvpMatrix: FloatArray, model: Loader, position: Vector2f = Vector2f(0f)) {

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, 0.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(model.model3d.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "uMVPMatrix"
        )
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "u_Texture"
        )
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[model.model3d.texture.id]
        )

        val mPositionHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "vPosition"
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            model.buffers.vertexBuffer
        )

        val mTexCoordHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "a_TexCoordinate"
        )
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(
            mTexCoordHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            model.buffers.texBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            model.buffers.indicesQty,
            GLES20.GL_UNSIGNED_SHORT,
            model.buffers.indicesBuffer
        )
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }

    fun drawHuman(mvpMatrix: FloatArray, model: Loader, position: Vector2f = Vector2f(0f), currentBonesPosesArray: FloatArray, variant: Int) {

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, 0.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(model.model3d.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "uMVPMatrix"
        )
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "u_Texture"
        )
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[model.model3d.texture.id]
        )

        val tex2Handler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "a_Texture"
        )
        GLES20.glUniform1i(tex2Handler, 1)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[model.model3d.textureAlpha?.id ?: 0]
        )

        val textureOffsetHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "textureOffset"
        )
        GLES20.glUniform1f(textureOffsetHandle, variant * 0.2246f)


        val bonesMatricesHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "bonesMatrices"
        )
        GLES20.glUniformMatrix4fv(
            bonesMatricesHandle,
            model.bones.size,
            false,
            currentBonesPosesArray,
            0
        )

        val mPositionHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "vPosition"
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            model.buffers.vertexBuffer
        )

        val mTexCoordHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "a_TexCoordinate"
        )
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(
            mTexCoordHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            model.buffers.texBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            model.buffers.indicesQty,
            GLES20.GL_UNSIGNED_SHORT,
            model.buffers.indicesBuffer
        )
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }


    fun draw(mvpMatrix: FloatArray, model: Loader, position: Vector2f = Vector2f(0f), currentBonesPosesArray: FloatArray) {

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, 0.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(model.model3d.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "uMVPMatrix"
        )
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "u_Texture"
        )
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[model.model3d.texture.id]
        )

        val bonesMatricesHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "bonesMatrices"
        )
        GLES20.glUniformMatrix4fv(
            bonesMatricesHandle,
            model.bones.size,
            false,
            currentBonesPosesArray,
            0
        )

        val mPositionHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "vPosition"
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            model.buffers.vertexBuffer
        )

        val mTexCoordHandle = GLES20.glGetAttribLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "a_TexCoordinate"
        )
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(
            mTexCoordHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            model.buffers.texBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            model.buffers.indicesQty,
            GLES20.GL_UNSIGNED_SHORT,
            model.buffers.indicesBuffer
        )
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }
}