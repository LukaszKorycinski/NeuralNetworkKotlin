package com.example.neuralnetworkkotlin.viewgroups

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.example.neuralnetworkkotlin.geometry.Vector3f

import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.PI

const val COORDS_PER_VERTEX = 3


class BackGround(context: Context) {

    val layerCoords = floatArrayOf(
        -10f, 10f, 0.0f,      // top left
        -10f, -20f, 0.0f,      // bottom left
        10f, -20f, 0.0f,      // bottom right
        10f, 10f, 0.0f       // top right
    )


    val textureCoords = floatArrayOf(
        1.0f, 0.0f,      // top left
        1.0f, 1.5f,      // bottom left
        0.0f, 1.5f,      // bottom right
        0.0f, 0.0f       // top right
    )


    var shaderLoader =
        ShaderLoader(context)

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    private val vertexBufferBackground: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(layerCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(layerCoords)
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
        ByteBuffer.allocateDirect(textureCoords.size * 4)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(textureCoords)
                    position(0)
                }
            }


    private var positionHandle: Int = 0

    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    var wave = 0f

    fun drawBackground(mvpMatrix: FloatArray, eyePosition: Vector3f, textures: TexturesLoader) {

        Matrix.translateM(mvpMatrix, 0, 0f, 0f, -5.0f)

        wave = wave + 0.025f
        if (wave > 2f * PI) wave = 0f


        for (i in 0..TexturesLoader.TEXTURES_QTY-1) {
            val zOffset = 1f * i
            //val rotMatrix = FloatArray(16)
            val mvptMatrix = FloatArray(16)
            val transMatrix = FloatArray(16)
            Matrix.setIdentityM(transMatrix, 0)
            Matrix.translateM(transMatrix, 0, 0f, 0f, zOffset)
            //Matrix.multiplyMM(modelMatrix, 0, transMatrix, 0, rotMatrix, 0)
            Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[i])

            val aspect = TexturesLoader.TEXTURES_QTY-1.toFloat() + 0.5f
            drawLayer(mvptMatrix, eyePosition, zOffset / aspect)
        }
    }

    fun drawLayer(mvpMatrix: FloatArray, eyePosition: Vector3f, modelPositionZ: Float) {
        GLES20.glUseProgram(shaderLoader.shaderProgram)
        var propertyHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(propertyHandler, 1, false, mvpMatrix, 0)

        propertyHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgram, "modelPositionIN")
        GLES20.glUniform1f(propertyHandler, 1.0f - modelPositionZ)

        propertyHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgram, "eyePosition")
        GLES20.glUniform3f(propertyHandler, eyePosition.x, eyePosition.y, eyePosition.z)

        propertyHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgram, "wave")
        GLES20.glUniform1f(propertyHandler, wave)


        positionHandle = GLES20.glGetAttribLocation(shaderLoader.shaderProgram, "vPosition").also {
            val mTextureCoordinateHandle =
                GLES20.glGetAttribLocation(shaderLoader.shaderProgram, "a_TexCoordinate")

            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)


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

            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferTrack
            )

            GLES20.glDisableVertexAttribArray(it)
        }
    }


}