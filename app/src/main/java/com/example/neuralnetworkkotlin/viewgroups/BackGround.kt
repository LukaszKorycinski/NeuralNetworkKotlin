package com.example.neuralnetworkkotlin.viewgroups

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.helpers.COORDS_PER_VERTEX
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class BackGround(context: Context) {
    val layerCoords = floatArrayOf(
        -10f, 10f, 0.0f,      // top left
        -10f, -10f, 0.0f,      // bottom left
        10f, -10f, 0.0f,      // bottom right
        10f, 10f, 0.0f       // top right
    )

    val textureCoords = floatArrayOf(
        1.0f, 0.0f,      // top left
        1.0f, 1.0f,      // bottom left
        0.0f, 1.0f,      // bottom right
        0.0f, 0.0f       // top right
    )


    var shaderLoader =
        ShaderLoader(context)

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    private val vertexBufferBackground: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(com.example.neuralnetworkkotlin.helpers.trackCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(com.example.neuralnetworkkotlin.helpers.trackCoords)
                    position(0)
                }
            }

    // initialize byte buffer for the draw list
    private val drawListBufferTrack: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }


    private val vertexBufferTextCoords: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(com.example.neuralnetworkkotlin.helpers.textureCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(com.example.neuralnetworkkotlin.helpers.textureCoords)
                    position(0)
                }
            }


    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex


    fun drawBackground(mvpMatrix: FloatArray, textures: TexturesLoader) {
        for (i in 0..6) {
            val zOffset = -1f*i
            //val rotMatrix = FloatArray(16)
            val modelMatrix = FloatArray(16)
            val transMatrix = FloatArray(16)
            Matrix.setIdentityM(transMatrix,0)
            Matrix.translateM(transMatrix, 0, 0f, 0f, zOffset)
            //Matrix.multiplyMM(modelMatrix, 0, transMatrix, 0, rotMatrix, 0)
            Matrix.multiplyMM(modelMatrix, 0, mvpMatrix, 0, transMatrix, 0)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[i])

            drawLayer(modelMatrix, transMatrix)
        }
    }

    fun drawLayer(mvpMatrix: FloatArray, uMMatrix: FloatArray) {


        GLES20.glUseProgram(shaderLoader.shaderProgram)
        shaderLoader.vPMatrixHandle = GLES20.glGetUniformLocation(shaderLoader.shaderProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(shaderLoader.vPMatrixHandle, 1, false, mvpMatrix, 0)

        shaderLoader.vPMatrixHandle = GLES20.glGetUniformLocation(shaderLoader.shaderProgram, "uMMatrix")
        GLES20.glUniformMatrix4fv(shaderLoader.vPMatrixHandle, 1, false, uMMatrix, 0)


        positionHandle = GLES20.glGetAttribLocation(shaderLoader.shaderProgram, "vPosition").also {
            val mTextureCoordinateHandle =
                GLES20.glGetAttribLocation(shaderLoader.shaderProgram, "a_TexCoordinate")
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
            GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                vertexBufferTextCoords
            )

            GLES20.glEnableVertexAttribArray(it)

            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBufferBackground
            )


            val colorToShader = floatArrayOf(0.0f, 0.0f, 0.2f, 1.0f)

            mColorHandle = GLES20.glGetUniformLocation(shaderLoader.shaderProgram, "vColor")
                .also { colorHandle ->
                    GLES20.glUniform4fv(colorHandle, 1, colorToShader, 0)
                }

            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferTrack
            )

            GLES20.glDisableVertexAttribArray(it)
        }
    }


}