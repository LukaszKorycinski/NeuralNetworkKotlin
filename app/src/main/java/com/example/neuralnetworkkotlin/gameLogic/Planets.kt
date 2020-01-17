package com.example.neuralnetworkkotlin.gameLogic

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import com.example.neuralnetworkkotlin.viewgroups.COORDS_PER_VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.PI

class Planets (context: Context) {

    val planetSize = 0.5f

    val layerCoords = floatArrayOf(
        -planetSize, planetSize, 0.0f,      // top left
        -planetSize, -planetSize, 0.0f,      // bottom left
        planetSize, -planetSize, 0.0f,      // bottom right
        planetSize, planetSize, 0.0f       // top right
    )


    val textureCoords = floatArrayOf(
        1.0f, 0.0f,      // top left
        1.0f, 1.0f,      // bottom left
        0.0f, 1.0f,      // bottom right
        0.0f, 0.0f       // top right
    )




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


    fun drawPlanet(mvpMatrix: FloatArray, eyePosition: Vector3f, textures: TexturesLoader, shader: Int) {
        val mvptMatrix = FloatArray(16)
        val transMatrix = FloatArray(16)
        Matrix.setIdentityM(transMatrix, 0)
        Matrix.translateM(transMatrix, 0, 0f, 0f, 10f)
        Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[0])

        GLES20.glUseProgram(shader)
        var propertyHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(propertyHandler, 1, false, mvptMatrix, 0)


        positionHandle = GLES20.glGetAttribLocation(shader, "vPosition").also {
            val mTextureCoordinateHandle =
                GLES20.glGetAttribLocation(shader, "a_TexCoordinate")

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