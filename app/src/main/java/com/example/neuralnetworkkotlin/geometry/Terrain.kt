package com.example.neuralnetworkkotlin.geometry

import android.opengl.GLES20
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import com.example.neuralnetworkkotlin.viewgroups.COORDS_PER_VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Terrain {

    val size = 10.0f

    val layerCoords = floatArrayOf(
        -size, 0.0f, size,       // top left
        -size, 0.0f, -size,      // bottom left
        size, 0.0f, -size,       // bottom right
        size, 0.0f, size,        // top right
    )


    val textureCoords = floatArrayOf(
        1.0f, 0.0f,      // top left
        1.0f, 1.0f,      // bottom left
        0.0f, 1f,      // bottom right
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







    fun drawTerrain(mvpMatrix: FloatArray, lightMatrix: FloatArray?, textureHandle: Int, shadowMapHandle: Int?, shader: Int) {

        GLES20.glUseProgram(shader)
        val uMVPMatrixHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(uMVPMatrixHandler, 1, false, mvpMatrix, 0)

        lightMatrix?.let{
            val lightMatrixHandler = GLES20.glGetUniformLocation(shader, "lightMatrix")
            GLES20.glUniformMatrix4fv(lightMatrixHandler, 1, false, lightMatrix, 0)
        }

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)

        shadowMapHandle?.let{
            val shadowmapHandler = GLES20.glGetUniformLocation(shader, "u_ShadowMap")
            GLES20.glUniform1i(shadowmapHandler, 1)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMapHandle)
        }



        positionHandle = GLES20.glGetAttribLocation(shader, "vPosition").also {
            val mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader, "a_TexCoordinate")

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




    var width: Int = 0
    var height = 0
    var nChannels = 0


    fun initHmap(){
        // vertex generation
        var vertices = floatArrayOf()

        for(i in 0..height)
        {
            for(j in 0..width)
            {

                // vertex
                vertices = vertices+(  );        // v.x
                vertices = vertices+(  ); // v.y
                vertices = vertices+(  );        // v.z
            }
        }
    }

    fun drawHmapTerrain(mvpMatrix: FloatArray, lightMatrix: FloatArray?, textureHandle: Int, shadowMapHandle: Int?, shader: Int){}
}