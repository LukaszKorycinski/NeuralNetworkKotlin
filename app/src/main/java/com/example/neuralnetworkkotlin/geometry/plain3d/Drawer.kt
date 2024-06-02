package com.example.neuralnetworkkotlin.geometry.plain3d

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.gameLogic.strategy.Human
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.helpers.scale
import com.example.neuralnetworkkotlin.helpers.translate
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f

class Drawer(val textures: TexturesLoader) {


    fun bindProgram(model: Loader) {
        GLES20.glUseProgram(ShaderLoader.getShaderProgram(model.model3d.shader))
    }

    fun drawBanner(mvpMatrix: FloatArray, model: Loader, position: Vector2f = Vector2f(0f), wave: Float) {

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, 0.0f, position.y)

        val waveHsndler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3d.shader),
            "wave"
        )
        GLES20.glUniform1f(waveHsndler, wave)

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

        draw(mvpMatrix, model, position)
    }


    fun draw(mvpMatrix: FloatArray, model: Loader, position: Vector2f = Vector2f(0f)) {

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, 0.0f, position.y)

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

    private val HUMAN_HEAD_OFFSET = 0.1542f

    fun drawHuman(
        mvpMatrix: FloatArray,
        model: Loader,
        human: Human,
        currentBonesPosesArray: FloatArray
    ) {
        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        tmpMatrix.translate(human.position.x, human.box.y, human.position.y)
        tmpMatrix.scale(human.look.direction.scaleX, 1.0f, 1.0f)

        val iVPMatrix = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "uMVPMatrix"
        )
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "u_Texture"
        )
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[model.model3da.texture.id]
        )

        val tex2Handler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "a_Texture"
        )
        GLES20.glUniform1i(tex2Handler, 1)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[model.model3da.textureAlpha?.id ?: 0]
        )

        val textureOffsetHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "textureOffset1"
        )
        GLES20.glUniform1f(textureOffsetHandle, human.look.variant.head * HUMAN_HEAD_OFFSET)
        val textureOffset2Handle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "textureOffset2"
        )
        GLES20.glUniform1f(textureOffset2Handle, human.look.variant.beard * HUMAN_HEAD_OFFSET)
        val skinColorHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "skinColor"
        )
        GLES20.glUniform1f(skinColorHandle, human.look.variant.skinColor)

        val bonesMatricesHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
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
            ShaderLoader.getShaderProgram(model.model3da.shader),
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
            ShaderLoader.getShaderProgram(model.model3da.shader),
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

    fun draw(
        mvpMatrix: FloatArray,
        model: Loader,
        position: Vector2f = Vector2f(0f),
        currentBonesPosesArray: FloatArray,
        direction: Float = 1f,
        offsetUP: Float = 0f
    ) {
        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        tmpMatrix.translate(position.x, offsetUP, position.y)
        tmpMatrix.scale(direction, 1.0f, 1.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(model.model3da.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "uMVPMatrix"
        )
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
            "u_Texture"
        )
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_2D,
            textures.textureHandle[model.model3da.texture.id]
        )

        val bonesMatricesHandle = GLES20.glGetUniformLocation(
            ShaderLoader.getShaderProgram(model.model3da.shader),
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
            ShaderLoader.getShaderProgram(model.model3da.shader),
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
            ShaderLoader.getShaderProgram(model.model3da.shader),
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